package com.matt.nocom.server.model.shared.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.MoreObjects;
import com.matt.nocom.server.model.sql.data.Position;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class LocationGroup implements Serializable, VectorXZ, WorldEntry {
  private String server;
  private int dimension;

  @Default
  private Region region = new Region();

  @Singular
  private List<Position> positions;

  public LocationGroup setup() {
    final Region region = getRegion();
    if(getPositions().isEmpty()) {
      region.setMaxX(Integer.MAX_VALUE);
      region.setMinX(Integer.MAX_VALUE);
      region.setMaxZ(Integer.MAX_VALUE);
      region.setMinZ(Integer.MAX_VALUE);
      return this;
    }

    for(Position position : getPositions()) {
      if(position.getX() > region.getMaxX() || region.getMaxX() == Integer.MAX_VALUE)
        region.setMaxX(position.getX());

      if(position.getX() < region.getMinX() || region.getMinX() == Integer.MAX_VALUE)
        region.setMinX(position.getX());

      if(position.getZ() > region.getMaxZ() || region.getMaxZ() == Integer.MAX_VALUE)
        region.setMaxZ(position.getZ());

      if(position.getZ() < region.getMinZ() || region.getMinZ() == Integer.MAX_VALUE)
        region.setMinZ(position.getZ());
    }

    return this;
  }

  @JsonInclude
  public int getX() {
    return getRegion().getMinX() + ((getRegion().getMaxX() - getRegion().getMinX()) / 2);
  }

  @JsonInclude
  public int getZ() {
    return getRegion().getMinZ() + ((getRegion().getMaxZ() - getRegion().getMinZ()) / 2);
  }

  private int distanceSqTo(int otherX, int otherZ) {
    return (int) Math.ceil(Math.pow(otherX - getX(), 2) + Math.pow(otherZ - getZ(), 2));
  }
  private int distanceSqTo(LocationGroup other) {
    return distanceSqTo(other.getX(), other.getZ());
  }

  public boolean isInRegion(LocationGroup other) {
    return getRegion().intersects(other.getRegion());
  }

  public boolean isInGroup(LocationGroup other, int distance) {
    return distanceSqTo(other) <= distance;
  }
  
  public static class LocationGroupBuilder {
    public LocationGroup build() {
      return new LocationGroup(server, dimension, MoreObjects.firstNonNull(region, new Region()), positions).setup();
    }
  }
}
