package com.matt.nocom.server.model.shared.data;

public interface VectorXZ {
  VectorXZ ORIGIN = new VectorXZ() {
    @Override
    public int getX() {
      return 0;
    }

    @Override
    public int getZ() {
      return 0;
    }
  };

  int getX();
  int getZ();

  default boolean isVectorEq(VectorXZ other) {
    return this == other || (other != null
        && getX() == other.getX()
        && getZ() == other.getZ());
  }

  default double distanceSqTo(VectorXZ other) {
    double dx = other.getX() - getX();
    double dz = other.getZ() - getZ();
    return (dx*dx) + (dz*dz);
  }

  default double distanceTo(VectorXZ other) {
    return Math.sqrt(distanceSqTo(other));
  }

  default double originDistanceSq() {
    return ORIGIN.distanceSqTo(this);
  }

  default double originDistance() {
    return ORIGIN.distanceTo(this);
  }
}
