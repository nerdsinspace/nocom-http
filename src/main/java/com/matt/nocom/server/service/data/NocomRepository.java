package com.matt.nocom.server.service.data;

import com.matt.nocom.server.model.shared.data.Dimension;
import com.matt.nocom.server.model.sql.data.Hit;
import com.matt.nocom.server.model.sql.data.SimpleHit;
import com.matt.nocom.server.model.sql.data.Track;
import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
        .fetch(record -> Dimension.from(record.getValue(DIMENSIONS.ORDINAL)));
  }

  @Transactional(readOnly = true)
  public List<Track> getMostRecentTracks(@NonNull String server, Duration duration) {
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
                .and(TRACKS.UPDATED_AT.gt(Instant.now().minus(duration).toEpochMilli()))))
        .fetch(record -> Track.builder()
            .trackId(record.getValue(HITS.TRACK_ID))
            .dimension(Dimension.from(record.getValue(HITS.DIMENSION)))
            .x(record.getValue(HITS.X) * 16)
            .z(record.getValue(HITS.Z) * 16)
            .createAt(Instant.ofEpochMilli(record.getValue(HITS.CREATED_AT)))
            .previousTrackId(record.getValue(TRACKS.ID.as("prev_track_id")))
            .previousDimension(Optional.ofNullable(record.getValue(TRACKS.DIMENSION.as("prev_dimension")))
                .map(Dimension::from)
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
            .dimension(Dimension.from(record.getValue(HITS.DIMENSION)))
            .build());
  }

  private Field<Integer> convertToOverworldCoord(Field<Integer> axis, Field<Short> dimension) {
    return axis.times(16).times(dimension.times(-7).plus(1));
  }
}
