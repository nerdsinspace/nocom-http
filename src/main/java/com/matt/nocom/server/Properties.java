package com.matt.nocom.server;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface Properties {
  int DISTANCE            = Integer.valueOf(System.getProperty("nocom.distance", "1000"));
  int VIEW_DISTANCE       = Integer.valueOf(System.getProperty("nocom.distance.view", String.valueOf(9 * 16)));

  String DATABASE_PATH    = System.getProperty("nocom.database", Paths.get("").resolve("locations.db").toAbsolutePath().toString());
  String RESOURCE_SQL     = "nocom.sql";
}
