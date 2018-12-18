package com.matt.nocom.server.controller;

import com.google.common.collect.Lists;
import com.matt.nocom.server.Logging;
import com.matt.nocom.server.model.Location;
import com.matt.nocom.server.service.SQLiteDatabase;
import com.matt.nocom.server.service.SQLiteStatements;
import com.matt.nocom.server.util.SQLFunction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

  private <T extends Statement> Location[] query(SQLFunction<Connection, T> supplier, SQLFunction<T, ResultSet> executor)
      throws SQLException {
    List<Location> locations = Lists.newArrayList();
    try(Connection connection = database.getConnection()) {
      T statement = supplier.apply(connection);
      try {
        ResultSet results = executor.apply(statement);
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
    return locations.toArray(new Location[0]);
  }

  @RequestMapping(value = "/locations", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ResponseEntity<Location[]> getLocations() throws SQLException {
    return ResponseEntity.ok(query(Connection::createStatement, statement -> statement.executeQuery(SQLiteStatements.SELECT_ALL_LOCATIONS)));
  }

  @RequestMapping(value = "/locations/{server}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ResponseEntity<Location[]> getLocationsByServer(@PathVariable String server,
      @RequestParam("startTime") Optional<Long> startTime,
      @RequestParam("endTime") Optional<Long> endTime) throws SQLException {
    Objects.requireNonNull(server, "Server parameter missing");
    return ResponseEntity.ok(query(
        conn -> conn.prepareStatement(SQLiteStatements.SELECT_LOCATIONS_WITH_FILTER),
        ps -> {
          ps.setString(1, server);
          ps.setLong(2, startTime.orElse(0L));
          ps.setLong(3, endTime.orElse(Long.MAX_VALUE));
          return ps.executeQuery();
        }));
  }
}
