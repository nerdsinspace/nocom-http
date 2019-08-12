package com.matt.nocom.server;

import java.nio.file.Paths;
import java.time.Duration;
import org.springframework.lang.Nullable;
import org.jooq.SQLDialect;

public interface Properties {
  
  /**
   * SQL Dialect to use for jOOQ
   */
  SQLDialect SQL_DIALECT = SQLDialect.SQLITE;
  
  /**
   * Enable to apply properties tokenizer to IDE builds
   */
  boolean DEV_MODE = Boolean.parseBoolean(System.getProperty("nocom.dev", "false"));
  
  /**
   * Authentication will use username:password provided by DEBUG_USERNAME/DEBUG_PASSWORD
   */
  boolean DEBUG_AUTH = Boolean.parseBoolean(System.getProperty("nocom.auth.debug", "false"));
  
  /**
   * Debug mode username and password
   */
  String DEBUG_USERNAME = System.getProperty("nocom.auth.debug.username", "root");
  String DEBUG_PASSWORD = System.getProperty("nocom.auth.debug.password", "pass");
  
  /**
   * A file that specifies a list of user=pass combinations
   */
  String ADMINS_FILE = System.getProperty("nocom.auth.admins", "admins.txt");
  
  /**
   * Time it takes for an access token to expire
   */
  long TOKEN_EXPIRATION = Long.parseLong(System.getProperty("nocom.login.expiration", String.valueOf(Duration.ofDays(1).toMillis()))); // default to 1 day
  
  /**
   * Minimum length of a password
   */
  int MIN_PASSWORD_LEN = Integer.parseInt(System.getProperty("nocom.min.pass.length", "8"));
  
  /**
   * World directory
   */
  String WORLDS_PATH = System.getProperty("nocom.worlds", Paths.get("worlds").toAbsolutePath().toString());
  
  /**
   * ? unused
   */
  @Nullable
  String SPIGOT_PATH = System.getProperty("nocom.spigot"); // path to jar
  
  /**
   * List of HTTP headers that may expose the clients real IP behind a reverse proxy
   */
  String[] HTTP_REAL_IP_HEADERS = System.getProperty("nocom.http.ip.headers", "X-REAL-IP").split(",");
}
