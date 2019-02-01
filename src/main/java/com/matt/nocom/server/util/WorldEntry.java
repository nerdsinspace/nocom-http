package com.matt.nocom.server.util;

public interface WorldEntry {
  String getServer();
  int getDimension();

  default boolean isInSameWorld(WorldEntry other) {
    return getServer().equalsIgnoreCase(other.getServer()) && getDimension() == other.getDimension();
  }
}
