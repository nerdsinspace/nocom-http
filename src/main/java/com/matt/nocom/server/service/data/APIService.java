package com.matt.nocom.server.service.data;

import static com.matt.nocom.server.sqlite.Tables.DIMENSIONS;
import static com.matt.nocom.server.sqlite.Tables.LOCATIONS;
import static com.matt.nocom.server.sqlite.Tables.POSITIONS;
import static com.matt.nocom.server.sqlite.Tables.SERVERS;

import com.matt.nocom.server.model.shared.data.Dimension;
import com.matt.nocom.server.model.sql.data.Location;
import com.matt.nocom.server.model.sql.data.Position;
import com.matt.nocom.server.model.http.data.SearchFilter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class APIService {
  private final DSLContext dsl;
  
  @Autowired
  public APIService(DSLContext dsl) {
    this.dsl = dsl;
  }

  public int[] addServers(Collection<String> servers) {
    return dsl.batch(
        servers.stream()
            .map(server -> dsl.insertInto(SERVERS, SERVERS.HOSTNAME)
                .values(server)
                .onDuplicateKeyIgnore())
            .collect(Collectors.toList())
    ).execute();
  }

  public int[] addPositions(Collection<Position> positions) {
    return dsl.batch(
        positions.stream()
            .map(position -> dsl.insertInto(POSITIONS, POSITIONS.X, POSITIONS.Z)
                .values(position.getX(), position.getZ())
                .onDuplicateKeyIgnore())
            .collect(Collectors.toList())
    ).execute();
  }

  public int[] addLocations(Collection<Location> locations) {
    return dsl.batch(
        locations.stream()
            .map(location -> dsl.insertInto(LOCATIONS,
                LOCATIONS.FOUND_TIME,
                LOCATIONS.UPLOAD_TIME,
                LOCATIONS.POS_ID,
                LOCATIONS.SERVER_ID,
                LOCATIONS.DIMENSION_ID)
                .values(
                    DSL.val(location.getTime()),
                    DSL.val(System.currentTimeMillis()),
                    DSL.field(dsl.select(POSITIONS.ID)
                        .from(POSITIONS)
                        .where(POSITIONS.X.eq(location.getX()))
                        .and(POSITIONS.Z.eq(location.getZ()))
                        .limit(1)),
                    DSL.field(dsl.select(SERVERS.ID)
                        .from(SERVERS)
                        .where(SERVERS.HOSTNAME.equalIgnoreCase(location.getServer()))
                        .limit(1)),
                    DSL.field(dsl.select(DIMENSIONS.ID)
                        .from(DIMENSIONS)
                        .where(DIMENSIONS.ORDINAL.eq(location.getDimension()))
                        .limit(1))
                )
                .onDuplicateKeyIgnore())
            .collect(Collectors.toList())
    ).execute();
  }

  public List<Location> getLocations(SearchFilter filter) {
    return dsl.select(
        LOCATIONS.FOUND_TIME,
        LOCATIONS.UPLOAD_TIME,
        SERVERS.HOSTNAME,
        POSITIONS.X,
        POSITIONS.Z,
        DIMENSIONS.ORDINAL)
        .from(LOCATIONS)
        .innerJoin(SERVERS).onKey()
        .innerJoin(POSITIONS).onKey()
        .innerJoin(DIMENSIONS).onKey()
        .where(filter.getConditions())
        .fetch()
        .map(record -> Location.builder()
            .time(record.getValue(LOCATIONS.FOUND_TIME))
            .uploadTime(record.getValue(LOCATIONS.UPLOAD_TIME))
            .server(record.getValue(SERVERS.HOSTNAME))
            .x(record.getValue(POSITIONS.X))
            .z(record.getValue(POSITIONS.Z))
            .dimension(record.getValue(DIMENSIONS.ORDINAL))
            .build());
  }

  public List<String> getServers() {
    return dsl.select(SERVERS.HOSTNAME)
        .from(SERVERS)
        .fetch()
        .map(record -> record.getValue(SERVERS.HOSTNAME));

  }

  public List<Dimension> getDimensions() {
    return dsl.select(DIMENSIONS.ORDINAL)
        .from(DIMENSIONS)
        .fetch()
        .map(record -> Dimension.from(record.getValue(DIMENSIONS.ORDINAL)));
  }
}
