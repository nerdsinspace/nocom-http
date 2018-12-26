package com.matt.nocom.server.controller;

import static com.matt.nocom.server.sqlite.Tables.DIMENSIONS;
import static com.matt.nocom.server.sqlite.Tables.LOCATIONS;
import static com.matt.nocom.server.sqlite.Tables.POSITIONS;
import static com.matt.nocom.server.sqlite.Tables.SERVERS;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.model.Dimension;
import com.matt.nocom.server.model.Location;
import com.matt.nocom.server.model.LocationGroup;
import com.matt.nocom.server.model.SearchFilter;
import com.matt.nocom.server.util.factory.LocationGroupFactory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("api")
public class APIController implements Logging {
  private final DSLContext dsl;

  @Autowired
  public APIController(DSLContext dsl) {
    this.dsl = dsl;
  }

  @RequestMapping(value = "/upload/locations",
      method = RequestMethod.POST,
      consumes = "application/json",
      produces = "application/json")
  @ResponseBody
  public ResponseEntity<Object> addLocations(@RequestBody Location[] locations) {
    if(locations.length < 1)
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Must have at least one location object.");

    dsl.batch(
        Arrays.stream(locations)
            .map(Location::getServer)
            .map(String::toLowerCase)
            .collect(Collectors.toSet()).stream() // unique values only
            .map(server -> dsl.insertInto(SERVERS, SERVERS.HOSTNAME)
                .values(server)
                .onDuplicateKeyIgnore())
            .collect(Collectors.toList())
    ).execute();

    dsl.batch(
        Arrays.stream(locations)
            .map(Location::getPosition)
            .collect(Collectors.toSet()).stream()
            .map(position -> dsl.insertInto(POSITIONS, POSITIONS.X, POSITIONS.Z)
                .values(position.getX(), position.getZ())
                .onDuplicateKeyIgnore())
            .collect(Collectors.toList())
    ).execute();

    dsl.batch(
        Arrays.stream(locations)
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

    return ResponseEntity.ok().body(null);
  }

  private List<Location> queryLocations(SearchFilter filter) {
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

  @RequestMapping(value = "/search/locations",
      method = RequestMethod.POST ,
      consumes = "application/json",
      produces = "application/json")
  @ResponseBody
  public ResponseEntity<Location[]> getLocations(@RequestBody SearchFilter filter) {
    return ResponseEntity.ok(queryLocations(filter).toArray(new Location[0]));
  }

  @RequestMapping(value = "/search/group/locations",
      method = RequestMethod.POST ,
      consumes = "application/json",
      produces = "application/json")
  @ResponseBody
  public ResponseEntity<LocationGroup[]> getLocationGroups(@RequestBody SearchFilter filter) {
    filter.forceGrouping();
    return ResponseEntity.ok(
        LocationGroupFactory.translate(queryLocations(filter), filter.getGroupingRange())
            .toArray(new LocationGroup[0]));
  }

  @RequestMapping(value = "/servers", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ResponseEntity<String[]> getServers() {
    return ResponseEntity.ok(
        dsl.select(SERVERS.HOSTNAME)
            .from(SERVERS)
            .fetch()
            .map(record -> record.getValue(SERVERS.HOSTNAME))
            .toArray(new String[0]));
  }

  @RequestMapping(value = "/dimensions", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ResponseEntity<Dimension[]> getDimensions() {
    return ResponseEntity.ok(
        dsl.select(DIMENSIONS.NAME, DIMENSIONS.ORDINAL)
            .from(DIMENSIONS)
            .fetch()
            .map(record -> Dimension.builder()
                .ordinal(record.getValue(DIMENSIONS.ORDINAL))
                .name(record.getValue(DIMENSIONS.NAME))
                .build())
            .toArray(new Dimension[0])
    );
  }
}
