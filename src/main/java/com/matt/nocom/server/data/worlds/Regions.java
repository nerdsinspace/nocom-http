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

  public Optional<Path> getRegionAt(int x, int z) {
    Path out = regionPath.resolve(String.format("r.%d.%d.mca", toRegion(x), toRegion(z)));

    return Files.exists(out) ? Optional.of(out) : Optional.empty();
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
