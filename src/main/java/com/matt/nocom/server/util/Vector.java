package com.matt.nocom.server.util;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vector implements VectorXZ {
  private int x;
  private int z;

  public Vector copy() {
    return new Vector(x, z);
  }

  @Override
  public int getX() {
    return x;
  }

  @Override
  public int getZ() {
    return z;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getX(), getZ());
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj || (obj instanceof VectorXZ
        && getX() == ((VectorXZ) obj).getX()
        && getZ() == ((VectorXZ) obj).getZ());
  }

  @Override
  public String toString() {
    return getX() + ", " + getZ();
  }
}
