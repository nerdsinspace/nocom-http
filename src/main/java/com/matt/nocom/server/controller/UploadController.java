package com.matt.nocom.server.controller;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.model.Location;
import com.matt.nocom.server.model.Position;
import com.matt.nocom.server.service.SQLiteDatabase;
import com.matt.nocom.server.service.SQLiteStatements;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("upload")
public class UploadController implements Logging {
  private final SQLiteDatabase database;

  public UploadController(SQLiteDatabase database) {
    this.database = database;
  }

  @RequestMapping(value = "/locations", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity<Object> addLocations(@RequestBody Location[] locations)
      throws SQLException {
    if(locations.length < 1)
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Must have at least one location object.");

    try(Connection connection = database.getConnection()) {
      connection.setAutoCommit(true);

      PreparedStatement statement;

      // insert all unique servers
      statement = connection.prepareStatement(SQLiteStatements.INSERT_SERVER);
      try {
        for (String server : Arrays.stream(locations)
            .map(Location::getServer)
            .map(String::toLowerCase)
            .collect(Collectors.toSet())) {
          statement.clearParameters();
          statement.setString(1, server);
          statement.addBatch();
        }
        statement.executeBatch();
      } finally {
        try {
          statement.close();
        } catch (SQLException e) {
          LOGGER.warn("Failed to close SQL statement", e);
        }
      }

      // insert all unique positions
      statement = connection.prepareStatement(SQLiteStatements.INSERT_POSITION);
      try {
        for (Position position : Arrays.stream(locations)
            .map(Location::getPosition)
            .collect(Collectors.toSet())) {
          statement.clearParameters();
          statement.setInt(1, position.getX());
          statement.setInt(2, position.getZ());
          statement.addBatch();
        }
        statement.executeBatch();
      } finally {
        try {
          statement.close();
        } catch (SQLException e) {
          LOGGER.warn("Failed to close SQL statement", e);
        }
      }

      // insert all locations
      statement = connection.prepareStatement(SQLiteStatements.INSERT_LOCATION);
      try {
        for (Location location : locations) {
          statement.clearParameters();
          statement.setLong(1, location.getTime());
          statement.setLong(2, System.currentTimeMillis());
          statement.setInt(3, location.getX());
          statement.setInt(4, location.getZ());
          statement.setString(5, location.getServer());
          statement.setInt(6, location.getDimension());
          statement.addBatch();
        }
        statement.executeBatch();
      } finally {
        try {
          statement.close();
        } catch (SQLException e) {
          LOGGER.warn("Failed to close SQL statement", e);
        }
      }
    }
    return ResponseEntity.ok().body(null);
  }
}
