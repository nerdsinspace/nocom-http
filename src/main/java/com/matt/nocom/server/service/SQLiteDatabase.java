package com.matt.nocom.server.service;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.stereotype.Component;

@Component
public class SQLiteDatabase implements Logging {
  public SQLiteDatabase() throws ClassNotFoundException, SQLException {
    Class.forName("org.sqlite.JDBC");

    try(Connection connection = getConnection()) {
      Statement statement = connection.createStatement();
      try {
        for (String sql : SQLiteStatements.getInstance().find("Init_*"))
          statement.execute(sql);
        statement.executeBatch();
      } finally {
        try {
          statement.close();
        } catch (SQLException e) {
          LOGGER.warn("Failed to close SQL statement", e);
        }
      }
    }
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection("jdbc:sqlite:" + Properties.DATABASE_PATH);
  }
}
