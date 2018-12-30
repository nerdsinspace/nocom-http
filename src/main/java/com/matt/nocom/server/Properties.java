package com.matt.nocom.server;

import java.io.File;
import java.nio.file.Paths;
import org.springframework.lang.Nullable;

public interface Properties {
  int DISTANCE            = Integer.valueOf(System.getProperty("nocom.distance", "1000"));
  int VIEW_DISTANCE       = Integer.valueOf(System.getProperty("nocom.distance.view", String.valueOf(9 * 16)));

  String DATABASE_PATH    = System.getProperty("nocom.database", Paths.get("").resolve("locations.db").toAbsolutePath().toString());

  String WORLDS_PATH      = System.getProperty("nocom.worlds", new File("worlds").getAbsolutePath());
  String RENDER_PATH      = System.getProperty("nocom.renders", new File("renders").getAbsolutePath());

  @Nullable
  String MAPCRAFTER_PATH  = System.getProperty("nocom.mapcrafter"); // path to exe
  @Nullable
  String SPIGOT_PATH      = System.getProperty("nocom.spigot"); // path to jar
}
