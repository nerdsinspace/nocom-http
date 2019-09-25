package com.matt.nocom.server.model.shared.data;

public interface WorldEntry {
  String getServer();
  int getDimension();

  default boolean isInSameWorld(WorldEntry other) {
    return getServer().equalsIgnoreCase(other.getServer()) && getDimension() == other.getDimension();
  }
}
