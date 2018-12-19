package com.matt.nocom.server.controller;

import static org.springframework.http.ResponseEntity.ok;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.matt.nocom.server.Logging;
import com.matt.nocom.server.model.GroupedLocation;
import com.matt.nocom.server.model.Location;
import com.matt.nocom.server.model.SearchFilter;
import com.matt.nocom.server.service.SQLiteDatabase;
import com.matt.nocom.server.service.SQLiteStatements;
import com.matt.nocom.server.util.LocationGrouper;
import com.matt.nocom.server.util.SQLFunction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("search")
public class SearchController implements Logging {
  private final SQLiteDatabase database;

  public SearchController(SQLiteDatabase database) {
    this.database = database;
  }

  @RequestMapping(value = "/locations", method = RequestMethod.POST , consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity<Object[]> getLocations(@RequestBody SearchFilter filter) throws SQLException {
    List<Location> locations = Lists.newArrayList();
    try(Connection connection = database.getConnection()) {
      String sql = (SQLiteStatements.SELECT_ALL_LOCATIONS + " " + filter.getQueryStatement()).trim();
      PreparedStatement statement = connection.prepareStatement(sql);
      try {
        filter.applyInserts(statement);
        ResultSet results = statement.executeQuery();
        try {
          while (results.next()) {
            locations.add(Location.builder()
                .x(results.getInt("x"))
                .z(results.getInt("z"))
                .dimension(results.getInt("dimension"))
                .server(results.getString("server"))
                .time(results.getLong("foundTime"))
                .uploadTime(results.getLong("uploadTime"))
                .build());
          }
        } finally {
          try {
            results.close();
          } catch (SQLException e) {
            LOGGER.warn("Failed to close SQL result set", e);
          }
        }
      } finally {
        try {
          statement.close();
        } catch (SQLException e) {
          LOGGER.warn("Failed to close SQL statement", e);
        }
      }
    }
    if(filter.getGroupingRange() == null)
      return ResponseEntity.ok(locations.toArray(new Location[0]));
    else
      return ResponseEntity.ok(new LocationGrouper(locations, filter.getGroupingRange())
          .getGroupedLocations()
          .toArray(new GroupedLocation[0]));
  }

  @RequestMapping(value = "/locations", method = RequestMethod.GET , produces = "application/json")
  @ResponseBody
  public ResponseEntity<Object[]> getLocations() throws SQLException {
    return getLocations(SearchFilter.builder().build());
  }

  @RequestMapping(value = "/locations/{server}/{dimension}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ResponseEntity<Object[]> getLocationsByServerAndDimension(@PathVariable String server,
      @PathVariable int dimension,
      @RequestParam("startTime") Optional<Long> startTime,
      @RequestParam("endTime") Optional<Long> endTime) throws SQLException {
    Objects.requireNonNull(server, "Server parameter missing");
    return getLocations(SearchFilter.builder()
        .server(server)
        .dimension(dimension)
        .startTime(startTime.orElse(null))
        .endTime(endTime.orElse(null))
        .build());
  }
}
