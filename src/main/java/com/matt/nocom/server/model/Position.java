package com.matt.nocom.server.model;

import java.util.Objects;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Position {
  private int x;
  private int z;

  @Override
  public boolean equals(Object obj) {
    return this == obj || (obj instanceof Position && getX() == ((Position) obj).getX() && getZ() == ((Position) obj).getZ());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getX(), getZ());
  }

  @Override
  public String toString() {
    return x + "," + z;
  }
}
