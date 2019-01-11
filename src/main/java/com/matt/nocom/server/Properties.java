package com.matt.nocom.server;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface Properties {
  boolean DEBUG_AUTH      = Boolean.valueOf(System.getProperty("nocom.debug.auth", "false"));

  int DISTANCE            = Integer.valueOf(System.getProperty("nocom.distance", "1000"));
  int VIEW_DISTANCE       = Integer.valueOf(System.getProperty("nocom.distance.view", String.valueOf(9 * 16)));

  String DATABASE_PATH    = System.getProperty("nocom.database", Paths.get("").resolve("locations.db").toAbsolutePath().toString());

  long TOKEN_EXPIRATION   = Long.valueOf(System.getProperty("nocom.login.expiration", "86400000")); // default to 1 day

  int MIN_PASSWORD_LEN    = Integer.valueOf(System.getProperty("nocom.min.pass.length", "8"));
}
