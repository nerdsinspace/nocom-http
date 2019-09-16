package com.matt.nocom.server.minecraft.world;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class MinecraftWorld {
  private final Path path;

  public MinecraftWorld(String name) {
    this.path = Paths.get("worlds").resolve(name);
  }

  public Dimensions ofType(Type type) {
    return new Dimensions(path.resolve(type.getDirName()));
  }

  @AllArgsConstructor(access = AccessLevel.PACKAGE)
  public class Dimensions {
    private Path path;

    public Regions dimension(int dimension) {
      return new Regions(path.resolve(String.valueOf(dimension)));
    }
  }


  @Getter
  @AllArgsConstructor
  // should be serialized as string as the identifier
  public enum Type {
    DOWNLOADED("Downloaded"),
    GENERATED("Generated");

    private String dirName;
  }
}
