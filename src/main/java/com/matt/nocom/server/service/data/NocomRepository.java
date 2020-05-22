package com.matt.nocom.server.service.data;

import com.matt.nocom.server.model.data.*;
import lombok.NonNull;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.matt.nocom.server.postgres.codegen.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
public class NocomRepository {
  private final DSLContext dsl;

  @Autowired
  public NocomRepository(@Qualifier("postgresDsl") DSLContext dsl) {
    this.dsl = dsl;
  }

  @Transactional(readOnly = true)
  public List<String> getServers() {
    return dsl.select(SERVERS.HOSTNAME)
        .from(SERVERS)
        .fetch(record -> record.getValue(SERVERS.HOSTNAME));
  }

  @Transactional(readOnly = true)
  public List<Dimension> getDimensions() {
    return dsl.select(DIMENSIONS.ORDINAL)
        .from(DIMENSIONS)
        .fetch(record -> Dimension.byOrdinal(record.getValue(DIMENSIONS.ORDINAL)));
  }

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

  @Transactional(readOnly = true)
  public List<SimpleHit> getTrackHistory(int trackId, long max) {
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

  @Transactional(readOnly = true)
  protected List<ClusterNode> getRootClusters(Condition condition) {
    return dsl.select(asterisk())
        .from(DBSCAN)
        .where(DBSCAN.DISJOINT_RANK.gt(0)
            .and(DBSCAN.CLUSTER_PARENT.isNull())
            .and(condition))
        .fetch(this::createClusterNode);
  }

  public List<ClusterNode> getRootClusters() {
    return getRootClusters(noCondition());
  }

  public List<ClusterNode> getRootClusters(String server, Dimension dimension) {
    var cond = noCondition();

    if (server != null) {
      cond = cond.and(DBSCAN.SERVER_ID.eq(
          select(SERVERS.ID)
              .from(SERVERS)
              .where(SERVERS.HOSTNAME.eq(server))
              .limit(1)));
    }

    if (dimension != null) {
      cond = cond.and(DBSCAN.DIMENSION.eq((short) dimension.getOrdinal()));
    }

    return getRootClusters(cond);
  }

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
                    .where(DBSCAN.as("clusters").DISJOINT_RANK.gt(0))
                )))
        .select(asterisk())
        .from(name("clusters"))
        .innerJoin(DBSCAN)
        .on(DBSCAN.ID.eq(DBSCAN.as("clusters").ID))
        .fetch(this::createClusterNode);
  }

  @Transactional(readOnly = true)
  public List<Player> getClusterPlayerAssociations(int clusterId) {
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
                    .where(DBSCAN.as("clusters").DISJOINT_RANK.gt(0))
                )))
        .select(asterisk())
        .from(select(ASSOCIATIONS.PLAYER_ID, sum(ASSOCIATIONS.ASSOCIATION).as("strength"))
            .from(ASSOCIATIONS)
            .innerJoin(name("clusters"))
            .on(ASSOCIATIONS.CLUSTER_ID.eq(DBSCAN.as("clusters").ID))
            .groupBy(ASSOCIATIONS.PLAYER_ID).asTable("assc"))
        .innerJoin(PLAYERS)
        .on(field(name("assc", "player_id"), int.class).eq(PLAYERS.ID))
        .orderBy(field(name("assc", "strength"), BigDecimal.class).desc())
        .fetch(record -> Player.builder()
            .username(record.get(PLAYERS.USERNAME))
            .uuid(record.get(PLAYERS.UUID))
            .strength(record.get(field(name("assc", "strength"), BigDecimal.class)).doubleValue())
            .build());
  }

  private ClusterNode createClusterNode(Record record) {
    return ClusterNode.builder()
        .id(record.get(DBSCAN.ID))
        .count(record.get(DBSCAN.CNT))
        .x(record.get(DBSCAN.X) * 16)
        .z(record.get(DBSCAN.Z) * 16)
        .dimension(Dimension.byOrdinal(record.get(DBSCAN.DIMENSION)))
        .core(record.get(DBSCAN.IS_CORE))
        .clusterParent(record.get(DBSCAN.CLUSTER_PARENT))
        .disjointRank(record.get(DBSCAN.DISJOINT_RANK))
        .disjointSize(record.get(DBSCAN.DISJOINT_SIZE))
        .build();
  }

  @Transactional(readOnly = true)
  public List<PlayerStatus> getBotStatuses() {
    return dsl.select(PLAYERS.USERNAME, PLAYERS.UUID,
        SERVERS.HOSTNAME, STATUSES.CURR_STATUS,
        STATUSES.UPDATED_AT, STATUSES.DATA)
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
            .build());
  }

//  public List<PlayerSession> getPlayerSessions(@NonNull UUID playerUuid,
//      @NonNull Instant from, @Nullable Instant to, @NonNull String server,
//      int max) {
//    to = MoreObjects.firstNonNull(to, Instant.now());
//    return dsl.select(PLAYER_SESSIONS.JOIN, PLAYER_SESSIONS.LEAVE, PLAYERS.UUID)
//        .from(PLAYER_SESSIONS)
//        .innerJoin(PLAYERS).on(PLAYERS.ID.eq(PLAYER_SESSIONS.PLAYER_ID))
//        .where(
//            PLAYER_SESSIONS.SERVER_ID.eq(select(SERVERS.ID)
//                .from(SERVERS)
//                .where(SERVERS.HOSTNAME.eq(server))
//                .limit(1))
//            .and(PLAYER_SESSIONS.JOIN.between(from.toEpochMilli(), to.toEpochMilli())
//                .or(PLAYER_SESSIONS.LEAVE.between(from.toEpochMilli(), to.toEpochMilli())))
//        )
//        .limit(range)
//  }
}
