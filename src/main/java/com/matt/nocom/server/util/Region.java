package com.matt.nocom.server.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
public class Region {
  private int minX;
  private int maxX;
  private int minZ;
  private int maxZ;

  public Region() {
    this(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
  }

  public boolean intersects(Region other) {
    return getMaxX() >= other.getMinX()
        && getMaxZ() >= other.getMinZ()
        && other.getMaxX() >= getMinX()
        && other.getMaxZ() >= getMinZ();
  }

  public boolean contains(VectorXZ vec) {
    return vec.getX() >= getMinX()
        && vec.getX() <= getMaxX()
        && vec.getZ() >= getMinZ()
        && vec.getZ() <= getMaxZ();
  }

  public double distanceSqTo(VectorXZ vec) {
    if(contains(vec))
      return 0.D;

    double dx = 0.D, dz = 0.D;
    if(vec.getX() < getMinX())
      dx = vec.getX() - getMinX();
    else if(vec.getX() > getMaxX())
      dx = vec.getX() - getMaxX();

    if(vec.getZ() < getMinZ())
      dz = vec.getZ() - getMinZ();
    else if(vec.getZ() > getMaxZ())
      dz = vec.getZ() - getMaxZ();

    return dx*dx + dz*dz;
  }

  public double distanceTo(VectorXZ vec) {
    return Math.sqrt(distanceSqTo(vec));
  }

  public Region copy() {
    return toBuilder().build();
  }
}
