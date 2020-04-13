package com.matt.nocom.server.service.data;

import com.matt.nocom.server.model.shared.data.Dimension;
import com.matt.nocom.server.model.sql.data.Track;
import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;
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
        .fetch()
        .map(record -> record.getValue(SERVERS.HOSTNAME));
  }

  @Transactional(readOnly = true)
  public List<Dimension> getDimensions() {
    return dsl.select(DIMENSIONS.ORDINAL)
        .from(DIMENSIONS)
        .fetch()
        .map(record -> Dimension.from(record.getValue(DIMENSIONS.ORDINAL)));
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
            HITS.X.times(16).times(HITS.DIMENSION.times(-7).plus(1)).as("overworld_block_x"),
            HITS.Z.times(16).times(HITS.DIMENSION.times(-7).plus(1)).as("overworld_block_z"),
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
                .and(TRACKS.UPDATED_AT.gt(Instant.now().minus(duration).toEpochMilli())))
        )
        .fetch(record -> Track.builder()
            .createAt(Instant.ofEpochMilli(record.getValue(HITS.CREATED_AT)))
            .chunkX(record.getValue(HITS.X))
            .chunkZ(record.getValue(HITS.Z))
            .overworldBlockX(record.getValue(HITS.X.as("overworld_block_x")))
            .overworldBlockZ(record.getValue(HITS.Z.as("overworld_block_z")))
            .dimension(Dimension.from(record.getValue(HITS.DIMENSION)))
            .previousTrackId(record.getValue(TRACKS.ID.as("prev_track_id")))
            .previousDimension(Optional.ofNullable(record.getValue(TRACKS.DIMENSION.as("prev_dimension")))
                .map(Dimension::from)
                .orElse(null))
            .build());
  }
}
