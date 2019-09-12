package com.matt.nocom.server;

import com.google.common.collect.Sets;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.regex.Pattern;
import org.jooq.SQLDialect;
import org.springframework.lang.Nullable;

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
  boolean DEBUG_AUTH = Boolean.parseBoolean(
      System.getProperty("nocom.auth.debug", "false"));
  
  /**
   * Debug mode username and password
   */
  String DEBUG_USERNAME = System.getProperty("nocom.auth.debug.username", "root");
  String DEBUG_PASSWORD = System.getProperty("nocom.auth.debug.password", "pass");
  
  /**
   * A file that specifies a list of user:pass combinations
   */
  String ADMINS_FILE = System.getProperty("nocom.auth.users.file", "users");
  
  /**
   * Time it takes for an access token to expire
   */
  long TOKEN_EXPIRATION = Long.parseLong(
      System.getProperty("nocom.auth.login.expiration",
          String.valueOf(Duration.ofDays(1).toMillis()))); // default to 1 day
  
  /**
   * Characters that are allowed in a username
   */
  Pattern USERNAME_ALLOWED_CHARACTERS = Pattern.compile(
      System.getProperty("nocom.auth.username.chars", "[A-Za-z0-9_]+"));
  
  /**
   * Minimum length of a username
   */
  int USERNAME_MIN_CHARACTERS = Integer.parseInt(
      System.getProperty("nocom.auth.username.min.length", "2"));
  
  /**
   * Usernames that are reserved
   */
  String[] USERNAME_RESERVED_NAMES = System.getProperty(
      "nocom.auth.username.reserved",
      String.join(",", Sets.newHashSet(DEBUG_USERNAME, "root"))
  ).split(",");
  
  /**
   * Minimum length of a password
   */
  int PASSWORD_MIN_CHARACTERS = Integer.parseInt(
      System.getProperty("nocom.auth.password.min.length", "8"));
  
  /**
   * Minimum level a user can be (not strict)
   */
  int USER_LEVEL_MIN = 0;
  
  /**
   * Maximum level a user can be (not strict)
   */
  int USER_LEVEL_MAX = 100;
  
  /**
   * World directory
   */
  String WORLDS_PATH = System.getProperty("nocom.worlds",
      Paths.get("worlds").toAbsolutePath().toString());
  
  /**
   * ? unused
   */
  @Nullable
  String SPIGOT_PATH = System.getProperty("nocom.spigot"); // path to jar
  
  /**
   * List of HTTP headers that may expose the clients real IP behind a reverse proxy
   */
  String[] HTTP_REAL_IP_HEADERS = System.getProperty("nocom.http.ip.proxy.headers",
      "X-REAL-IP").split(",");
}
