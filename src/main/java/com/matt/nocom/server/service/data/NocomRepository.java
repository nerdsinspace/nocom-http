package com.matt.nocom.server.service.data;

import com.google.common.base.MoreObjects;
import com.matt.nocom.server.model.data.*;
import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Record1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.matt.nocom.server.postgres.codegen.Tables.*;
import static org.jooq.impl.DSL.*;
import static space.nerdsin.nocom.server.jooq.DSLRangeOperations.rangeIncludes;
import static space.nerdsin.nocom.server.jooq.DSLRangeOperations.upperRange;

@Repository
public class NocomRepository {
  private final DSLContext dsl;

  @Autowired
  public NocomRepository(@Qualifier("postgresDsl") DSLContext dsl) {
    this.dsl = dsl;
  }

  /**
   * A set of the servers that currently exist in the database
   *
   * @return a set containing unique server hostnames
   */
  @Transactional(readOnly = true)
  public Set<String> getServers() {
    return dsl.selectDistinct(SERVERS.HOSTNAME)
        .from(SERVERS)
        .fetchSet(SERVERS.HOSTNAME);
  }

  /**
   * A set of dimensions in the database
   *
   * @return a distinct set of dimension enums
   */
  @Transactional(readOnly = true)
  public Set<Dimension> getDimensions() {
    return dsl.selectDistinct(DIMENSIONS.ORDINAL)
        .from(DIMENSIONS)
        .fetchStream()
        .map(Record1::component1)
        .map(Dimension::byOrdinal)
        .collect(Collectors.toSet());
  }

  /**
   * Gets a list of tracks that existed ranging from @time to @time - @duration
   *
   * @param server   The server to check tracks for
   * @param time     The upper limit of time
   * @param duration The duration in the past to check relative to @time
   * @return A list of unique tracks and their latest entry in the provided time span
   */
  @Transactional(readOnly = true)
  public List<Track> getMostRecentTracks(@NonNull String server, Instant time, Duration duration) {
    return dsl.select(asterisk())
        .from(select(
            HITS.CREATED_AT,
            HITS.X,
            HITS.Z,
            HITS.DIMENSION,
            HITS.TRACK_ID,
            TRACKS.as("prev").ID.as("prev_track_id"),
            TRACKS.as("prev").DIMENSION.as("prev_dimension"))
            .from(TRACKS)
            .leftOuterJoin(HITS)
            .on(TRACKS.LAST_HIT_ID.eq(HITS.ID))
            .leftOuterJoin(TRACKS.as("prev"))
            .on(TRACKS.as("prev").ID.eq(TRACKS.PREV_TRACK_ID))
            .where(TRACKS.SERVER_ID.eq(
                select(SERVERS.ID)
                    .from(SERVERS)
                    .where(SERVERS.HOSTNAME.eq(server))
                    .limit(1))
                .and(HITS.TRACK_ID.isNotNull())
                .and(TRACKS.UPDATED_AT.gt(time.minus(duration).toEpochMilli()))))
        .fetch(record -> Track.builder()
            .trackId(record.getValue(HITS.TRACK_ID))
            .dimension(Dimension.byOrdinal(record.getValue(HITS.DIMENSION)))
            .x(record.getValue(HITS.X) * 16)
            .z(record.getValue(HITS.Z) * 16)
            .createAt(Instant.ofEpochMilli(record.getValue(HITS.CREATED_AT)))
            .previousTrackId(record.getValue(TRACKS.ID.as("prev_track_id")))
            .previousDimension(Optional.ofNullable(record.getValue(TRACKS.DIMENSION.as("prev_dimension")))
                .map(Dimension::byOrdinal)
                .orElse(null))
            .build());
  }

  /**
   * Get all the hits associated with a track id
   *
   * @param trackId Target track id
   * @param max     Maximum number of tracks the query should return.
   * @return A list of associated hits for the track ordered by their creation time
   */
  @Transactional(readOnly = true)
  public List<SimpleHit> getTrackHistory(int trackId, Long max) {
    return dsl.select(HITS.X, HITS.Z)
        .from(HITS)
        .where(HITS.TRACK_ID.eq(trackId))
        .orderBy(HITS.CREATED_AT.desc())
        .limit(max)
        .fetch(record -> SimpleHit.builder()
            .x(record.getValue(HITS.X) * 16)
            .z(record.getValue(HITS.Z) * 16)
            .build());
  }

  /**
   * Get all the hits associated with a track and the previous tracks associated.
   * This query can be massive if the track history is very long.
   *
   * @param trackId Target track id
   * @param max     Maximum number of tracks the query should return
   * @return A list of every hit in the track history ordered by their creation time
   */
  @Transactional(readOnly = true)
  public List<Hit> getFullTrackHistory(int trackId, long max) {
    return dsl.withRecursive("track_hist")
        .as(select(val(trackId).as("track_id"))
            .union(select(TRACKS.as("t").PREV_TRACK_ID)
                .from(TRACKS.as("t"))
                .innerJoin(name("track_hist"))
                .on(field(name("track_hist", "track_id"), int.class)
                    .eq(TRACKS.as("t").ID))))
        .select(
            field(name("track_hist", "track_id"), int.class),
            HITS.CREATED_AT,
            HITS.X,
            HITS.Z,
            HITS.DIMENSION)
        .from(HITS)
        .innerJoin(name("track_hist"))
        .on(field(name("track_hist", "track_id"), int.class)
            .eq(HITS.TRACK_ID))
        .where(HITS.TRACK_ID.isNotNull())
        .orderBy(HITS.CREATED_AT.desc())
        .limit(max)
        .fetch(record -> Hit.builder()
            .trackId((Integer) record.getValue("track_id"))
            .createdAt(Instant.ofEpochMilli(record.getValue(HITS.CREATED_AT)))
            .x(record.getValue(HITS.X) * 16)
            .z(record.getValue(HITS.Z) * 16)
            .dimension(Dimension.byOrdinal(record.getValue(HITS.DIMENSION)))
            .build());
  }

  /**
   * Get all the root cluster nodes
   *
   * @param server    Target server
   * @param dimension Target dimension
   * @return A list of root nodes associated with the server and dimension
   */
  @Transactional(readOnly = true)
  public List<ClusterNode> getRootClusters(@NonNull String server, @NonNull Dimension dimension) {
    return dsl.selectFrom(DBSCAN)
        .where(DBSCAN.DISJOINT_RANK.gt(0)
            .and(DBSCAN.CLUSTER_PARENT.isNull())
            .and(DBSCAN.SERVER_ID.eq(
                select(SERVERS.ID)
                    .from(SERVERS)
                    .where(SERVERS.HOSTNAME.eq(server))
                    .limit(1)))
            .and(DBSCAN.DIMENSION.eq(dimension.getOrdinalAsShort())))
        .orderBy(DBSCAN.ROOT_UPDATED_AT)
        .fetch(this::createClusterNode);
  }

  /**
   * Get all the children nodes associated with a root node
   *
   * @param clusterId Root cluster node id
   * @return A list of children nodes for the root node
   */
  @Transactional(readOnly = true)
  public List<ClusterNode> getFullCluster(int clusterId) {
    return dsl.withRecursive("tmp")
        .as(select(DBSCAN.ID, DBSCAN.DISJOINT_RANK)
            .from(DBSCAN)
            .where(DBSCAN.ID.eq(clusterId)))
        .with(name("clusters")
            .as(select(asterisk())
                .from(name("tmp"))
                .union(select(DBSCAN.ID, DBSCAN.DISJOINT_RANK)
                    .from(DBSCAN)
                    .innerJoin(name("clusters"))
                    .on(DBSCAN.CLUSTER_PARENT.eq(DBSCAN.as("clusters").ID))
                    .where(DBSCAN.as("clusters").DISJOINT_RANK.gt(0)))))
        .select(asterisk())
        .from(name("clusters"))
        .innerJoin(DBSCAN)
        .on(DBSCAN.ID.eq(DBSCAN.as("clusters").ID))
        .fetch(this::createClusterNode);
  }

  /**
   * Get the players associated with a cluster
   *
   * @param clusterId root cluster id
   * @return A list of players associated with the given cluster id
   */
  @Transactional(readOnly = true)
  public List<Player> getClusterPlayerAssociations(int clusterId) {
    final var ASSC_PLAYER_ID = field(name("assc", "player_id"), int.class);
    final var ASSC_STRENGTH = field(name("assc", "strength"), BigDecimal.class);
    return dsl.withRecursive("tmp")
        .as(select(DBSCAN.ID, DBSCAN.DISJOINT_RANK)
            .from(DBSCAN)
            .where(DBSCAN.ID.eq(clusterId)))
        .with(name("clusters")
            .as(select(asterisk())
                .from(name("tmp"))
                .union(select(DBSCAN.ID, DBSCAN.DISJOINT_RANK)
                    .from(DBSCAN)
                    .innerJoin(name("clusters"))
                    .on(DBSCAN.CLUSTER_PARENT.eq(DBSCAN.as("clusters").ID))
                    .where(DBSCAN.as("clusters").DISJOINT_RANK.gt(0)))))
        .select(asterisk())
        .from(select(ASSOCIATIONS.PLAYER_ID, sum(ASSOCIATIONS.ASSOCIATION).as("strength"))
            .from(ASSOCIATIONS)
            .innerJoin(name("clusters"))
            .on(ASSOCIATIONS.CLUSTER_ID.eq(DBSCAN.as("clusters").ID))
            .groupBy(ASSOCIATIONS.PLAYER_ID).asTable("assc"))
        .innerJoin(PLAYERS)
        .on(ASSC_PLAYER_ID.eq(PLAYERS.ID))
        .orderBy(ASSC_STRENGTH.desc())
        .fetch(record -> Player.builder()
            .username(record.get(PLAYERS.USERNAME))
            .uuid(record.get(PLAYERS.UUID))
            .strength(record.get(ASSC_STRENGTH).doubleValue())
            .build());
  }

  /**
   * Get the clusters associated with a player
   *
   * @param playerUsername playerUsername
   * @return A list of root clusters associated with the given player username
   */
  @Transactional(readOnly = true)
  public List<Association> getPlayerClusterAssociations(String playerUsername) {
    final var ASSOCIATIONS_STRENGTH = field(name("strength"), BigDecimal.class);
    final var ASSOCIATIONS_LAST_SEEN = field(name("last_seen"), long.class);

    return dsl.select(ASSOCIATIONS.CLUSTER_ID, sum(ASSOCIATIONS.ASSOCIATION).as("strength"), max(ASSOCIATIONS.CREATED_AT).as("last_seen"))
        .from(ASSOCIATIONS)
        .where(ASSOCIATIONS.PLAYER_ID.eq(select(PLAYERS.ID).from(PLAYERS).where(lower(PLAYERS.USERNAME).like(playerUsername))))
        .groupBy(ASSOCIATIONS.CLUSTER_ID)
        .orderBy(ASSOCIATIONS_STRENGTH.desc()).fetch(record -> Association.builder()
            .clusterId(record.get(ASSOCIATIONS.CLUSTER_ID))
            .strength(record.get(ASSOCIATIONS_STRENGTH))
            .lastSeen(record.get(ASSOCIATIONS_LAST_SEEN))
            .build());
  }

  private ClusterNode createClusterNode(Record record) {
    return ClusterNode.builder()
        .id(record.get(DBSCAN.ID))
        .count(record.get(DBSCAN.DISJOINT_SIZE))
        .x(record.get(DBSCAN.X) * 16)
        .z(record.get(DBSCAN.Z) * 16)
        .dimension(Dimension.byOrdinal(record.get(DBSCAN.DIMENSION)))
        .core(record.get(DBSCAN.IS_CORE))
        .clusterParent(record.get(DBSCAN.CLUSTER_PARENT))
        .disjointRank(record.get(DBSCAN.DISJOINT_RANK))
        .disjointSize(record.get(DBSCAN.DISJOINT_SIZE))
        .updatedAt(Optional.ofNullable(record.get(DBSCAN.ROOT_UPDATED_AT))
            .map(Instant::ofEpochMilli)
            .orElse(null))
        .build();
  }

  /**
   * Get the status of the bots. Status data includes username, uuid, server connected to, status (online, offline,
   * queue), last time update made, data (queue position), and dimension.
   *
   * @return List of bot statuses
   */
  @Transactional(readOnly = true)
  public List<PlayerStatus> getBotStatuses() {
    return dsl.select(PLAYERS.USERNAME, PLAYERS.UUID,
        SERVERS.HOSTNAME, STATUSES.CURR_STATUS,
        STATUSES.UPDATED_AT, STATUSES.DATA, STATUSES.DIMENSION)
        .from(STATUSES)
        .innerJoin(PLAYERS)
        .on(PLAYERS.ID.eq(STATUSES.PLAYER_ID))
        .innerJoin(SERVERS)
        .on(SERVERS.ID.eq(STATUSES.SERVER_ID))
        .fetch(record -> PlayerStatus.builder()
            .playerUsername(record.get(PLAYERS.USERNAME))
            .playerUuid(record.get(PLAYERS.UUID))
            .server(record.get(SERVERS.HOSTNAME))
            .state(record.get(STATUSES.CURR_STATUS))
            .updatedAt(Instant.ofEpochMilli(record.get(STATUSES.UPDATED_AT)))
            .data(record.get(STATUSES.DATA))
            .dimension(Dimension.byOrdinal(MoreObjects.firstNonNull(record.get(STATUSES.DIMENSION), (short)0)))
            .build());
  }

  /**
   * Get the players sessions history from now until the given duration in the past.
   *
   * @param server      Servers to get session history for
   * @param history     Time in past to lookup
   * @param playerUuids A list of player uuids to get session history for
   * @return A list of player session data
   */
  @Transactional(readOnly = true)
  public List<PlayerSession> getPlayerSessions(final String server, final Duration history, UUID... playerUuids) {
    final var duration = Instant.now().minus(MoreObjects.firstNonNull(history, Duration.ZERO));
    final var selectSid = select(field(name("id"), short.class))
        .from(name("sid"));
    final var selectPid  = select(field(name("id"), int.class))
        .from(name("pid"));
    return Arrays.stream(playerUuids)
        .flatMap(uuid -> dsl.with("pid")
            .as(select(PLAYERS.ID.as("id"))
                .from(PLAYERS)
                .where(PLAYERS.UUID.eq(uuid))
                .limit(1))
            .with("sid")
            .as(select(SERVERS.ID.as("id"))
                .from(SERVERS)
                .where(SERVERS.HOSTNAME.eq(server))
                .limit(1))
            .with("tmp")
            .as(select(max(upperRange(PLAYER_SESSIONS.RANGE, long.class)).as("max"))
                .from(PLAYER_SESSIONS)
                .where(PLAYER_SESSIONS.SERVER_ID.eq(selectSid))
                .and(PLAYER_SESSIONS.PLAYER_ID.eq(selectPid))
                .and(upperRange(PLAYER_SESSIONS.RANGE, long.class).lt(val(duration.toEpochMilli()))))
            .select(PLAYERS.USERNAME, PLAYERS.UUID, PLAYER_SESSIONS.JOIN, PLAYER_SESSIONS.LEAVE)
            .from(PLAYER_SESSIONS)
            .innerJoin(PLAYERS).on(PLAYER_SESSIONS.PLAYER_ID.eq(PLAYERS.ID))
            .where(PLAYER_SESSIONS.SERVER_ID.eq(selectSid))
            .and(PLAYER_SESSIONS.PLAYER_ID.eq(selectPid))
            .and(upperRange(PLAYER_SESSIONS.RANGE, long.class).ge(select(field(name("max"), long.class)).from(name("tmp"))))
            .fetchStream()
            .map(record -> PlayerSession.builder()
                .username(record.get(PLAYERS.USERNAME))
                .uuid(record.get(PLAYERS.UUID))
                .join(convertMsToInstant(record.get(PLAYER_SESSIONS.JOIN)))
                .leave(convertMsToInstant(record.get(PLAYER_SESSIONS.LEAVE)))
                .build()))
        .collect(Collectors.toList());
  }

  /**
   * Get the time a player connected, provided they are currently connected to the server.
   *
   * @param server  server hostname
   * @param players UUIDs of players
   * @return A list of player sessions with the join time
   */
  @Transactional(readOnly = true)
  public List<PlayerSession> getPlayersConnectTime(@NonNull final String server, UUID... players) {
    return Arrays.stream(players)
        .flatMap(uuid -> dsl.select(asterisk())
            .from(PLAYER_SESSIONS)
            .innerJoin(PLAYERS).on(PLAYER_SESSIONS.PLAYER_ID.eq(PLAYERS.ID))
            .where(PLAYERS.UUID.eq(uuid))
            .and(PLAYER_SESSIONS.SERVER_ID.eq(select(SERVERS.ID)
                .from(SERVERS)
                .where(SERVERS.HOSTNAME.eq(server))
                .limit(1)))
            .and(rangeIncludes(PLAYER_SESSIONS.RANGE, Long.MAX_VALUE - 1))
            .fetchStream()
            .map(record -> PlayerSession.builder()
                .username(record.get(PLAYERS.USERNAME))
                .uuid(record.get(PLAYERS.UUID))
                .join(convertMsToInstant(record.get(PLAYER_SESSIONS.JOIN)))
                .build()))
        .collect(Collectors.toList());
  }

  /**
   * Get the last connection time of a player. If they are currently connected, the leave time will be omitted.
   *
   * @param server  server hostname
   * @param players uuids of players
   * @return A list of player sessions
   */
  @Transactional(readOnly = true)
  public List<PlayerSession> getPlayersLatestSession(@NonNull final String server, UUID... players) {
    final var selectSid = select(field(name("id"), short.class))
        .from(name("sid"));
    final var selectPid  = select(field(name("id"), int.class))
        .from(name("pid"));
    return Arrays.stream(players)
        .flatMap(uuid -> dsl.with("pid")
            .as(select(PLAYERS.ID.as("id"))
                .from(PLAYERS)
                .where(PLAYERS.UUID.eq(uuid))
                .limit(1))
            .with("sid")
            .as(select(SERVERS.ID.as("id"))
                .from(SERVERS)
                .where(SERVERS.HOSTNAME.eq(server))
                .limit(1))
            .with("tmp")
            .as(select(max(upperRange(PLAYER_SESSIONS.RANGE, long.class)).as("max"))
                .from(PLAYER_SESSIONS)
                .where(PLAYER_SESSIONS.SERVER_ID.eq(selectSid))
                .and(PLAYER_SESSIONS.PLAYER_ID.eq(selectPid)))
            .select(PLAYERS.USERNAME, PLAYERS.UUID, PLAYER_SESSIONS.JOIN, PLAYER_SESSIONS.LEAVE)
            .from(PLAYER_SESSIONS)
            .innerJoin(PLAYERS).on(PLAYER_SESSIONS.PLAYER_ID.eq(PLAYERS.ID))
            .where(PLAYERS.ID.eq(selectPid))
            .and(upperRange(PLAYER_SESSIONS.RANGE, long.class).ge(select(field(name("max"), long.class)).from(name("tmp"))))
            .and(PLAYER_SESSIONS.SERVER_ID.eq(selectSid))
            .fetchStream()
            .map(record -> PlayerSession.builder()
                .username(record.get(PLAYERS.USERNAME))
                .uuid(record.get(PLAYERS.UUID))
                .join(convertMsToInstant(record.get(PLAYER_SESSIONS.JOIN)))
                .leave(convertMsToInstant(record.get(PLAYER_SESSIONS.LEAVE)))
                .build()))
        .collect(Collectors.toList());
  }

  private static Instant convertMsToInstant(Long time) {
    return Optional.ofNullable(time)
        .filter(ms -> ms <= Long.MAX_VALUE - 1)
        .map(Instant::ofEpochMilli)
        .orElse(null);
  }

  // TODO: In jOOQ 3.14, materialized will be added
  private Query query_getBlockCountInCluster(int clusterId, String blockName) {
    final var CLUSTERS = table(name("clusters"));
    final var CLUSTERS_id = field(name(CLUSTERS.getName(), "id"), int.class);
    final var CLUSTERS_disjointRank = field(name(CLUSTERS.getName(), "disjoint_rank"), int.class);
    final var FILTER = table(name("filter"));
    final var AGE = field(name("age"), int.class);
    final var NUM_BLOCKS = field(name("num_blocks"), BigDecimal.class);
    final var BS = field(name("bs"));
    return dsl.withRecursive("tmp")
        .as(select(DBSCAN.ID, DBSCAN.DISJOINT_RANK)
            .from(DBSCAN)
            .where(DBSCAN.ID.eq(clusterId)))
        .with(CLUSTERS.getName())
        .as(select(asterisk())
            .from(name("tmp"))
            .union(select(DBSCAN.ID, DBSCAN.DISJOINT_RANK)
                .from(DBSCAN)
                .innerJoin(CLUSTERS)
                .on(DBSCAN.CLUSTER_PARENT.eq(CLUSTERS_id))
                .where(CLUSTERS_disjointRank.gt(0))))
        .with(FILTER.getName())
        .as(select(BLOCK_STATES.BLOCK_STATE) // this needs to be materialized
            .from(BLOCK_STATES)
            .where(BLOCK_STATES.NAME.like(blockName)))
        .select(sum(NUM_BLOCKS))
        .from(select(select(count(asterisk()))
            .from(select(BLOCKS.BLOCK_STATE.as(BS),
                rowNumber().over(partitionBy(BLOCKS.X, BLOCKS.Y, BLOCKS.Z).orderBy(BLOCKS.CREATED_AT.desc())).as(AGE))
                .from(BLOCKS)
                .where(BLOCKS.X.shr(4).eq(DBSCAN.X))
                .and(BLOCKS.Z.shr(4).eq(DBSCAN.Z)))
            .where(BS.in(selectFrom(FILTER)))
            .and(AGE.eq(1))
            .asField(NUM_BLOCKS.getName()), DBSCAN.X, DBSCAN.Z)
            .from(CLUSTERS)
            .innerJoin(DBSCAN).on(DBSCAN.ID.eq(CLUSTERS_disjointRank)));
  }

  /**
   * Get the count of blocks in a cluster.
   * WARNING: This operation can be very slow!
   * TODO: In jOOQ 3.14 "WITH MATERIALIZED" is added, so I can remove the disgusting string replacement
   *
   * @param clusterId root cluster id
   * @param blockName name of block
   * @return The number of blocks inside the cluster
   */
  @Transactional(readOnly = true)
  public Optional<Long> getBlockCountInCluster(int clusterId, String blockName) {
    // jooq doesnt support materialized until 3.14
    var q = query_getBlockCountInCluster(clusterId, blockName);
    var sql = q.getSQL().replace("\"filter\" as", "\"filter\" as materialized");
    return dsl.fetch(sql, q.getBindValues().toArray())
        .getValues(name("num_blocks"), BigDecimal.class).stream()
        .map(BigDecimal::longValue)
        .findAny();
  }
}
