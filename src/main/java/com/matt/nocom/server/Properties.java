package com.matt.nocom.server;

import java.nio.file.Paths;
import org.springframework.lang.Nullable;

public interface Properties {
  boolean DEBUG_AUTH      = Boolean.valueOf(System.getProperty("nocom.debug.auth", "false"));

  int DISTANCE            = Integer.valueOf(System.getProperty("nocom.distance", "1000"));
  int VIEW_DISTANCE       = Integer.valueOf(System.getProperty("nocom.distance.view", String.valueOf(9 * 16)));

  String DATABASE_PATH    = System.getProperty("nocom.database", Paths.get("locations.db").toAbsolutePath().toString());

  long TOKEN_EXPIRATION   = Long.valueOf(System.getProperty("nocom.login.expiration", "86400000")); // default to 1 day

  int MIN_PASSWORD_LEN    = Integer.valueOf(System.getProperty("nocom.min.pass.length", "8"));


  String WORLDS_PATH      = System.getProperty("nocom.worlds", Paths.get("worlds").toAbsolutePath().toString());
  String RENDER_PATH      = System.getProperty("nocom.renders", Paths.get("renders").toAbsolutePath().toString());

  @Nullable
  String MAPCRAFTER_PATH  = System.getProperty("nocom.mapcrafter"); // path to exe
  @Nullable
  String SPIGOT_PATH      = System.getProperty("nocom.spigot"); // path to jar


}
