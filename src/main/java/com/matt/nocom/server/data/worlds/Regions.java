package com.matt.nocom.server.data.worlds;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

// path to actual data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Regions {
  private final Path regionPath;

  // may or may not exist
  public Path getRegionAt(int x, int z) {
    return regionPath.resolve(String.format("r.%d.%d.mca", toRegion(x), toRegion(z)));
  }

  public boolean hasRegionAt(int x, int z) {
    return Files.exists(getRegionAt(x, z));
  }

  public Path getPath() {
    return this.regionPath;
  }

  private int toRegion(int x) {
    // block -> chunk = x >> 4
    // chunk -> region = x >> 5
    // block -> region = x >> (4 + 5)
    return x >> 9;
  }

}
