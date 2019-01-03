package com.matt.nocom.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
public class LocationGroup implements Serializable {
  private int x;
  private int z;
  private String server;
  private int dimension;

  @Singular
  private List<Position> positions;

  public LocationGroup setup() {
    setX(getPositions().stream().collect(Collectors.averagingInt(Position::getX)).intValue());
    setZ(getPositions().stream().collect(Collectors.averagingInt(Position::getZ)).intValue());
    return this;
  }

  private int distanceSqTo(int otherX, int otherZ) {
    return (int) Math.ceil(Math.pow(otherX - getX(), 2) + Math.pow(otherZ - getZ(), 2));
  }
  private int distanceSqTo(LocationGroup other) {
    return distanceSqTo(other.getX(), other.getZ());
  }

  @JsonIgnore
  public boolean isInGroup(LocationGroup other, int distance) {
    return distanceSqTo(other) <= distance;
  }

  @JsonIgnore
  public boolean isInSameWorld(LocationGroup other) {
    return getServer().equalsIgnoreCase(other.getServer()) && getDimension() == other.getDimension();
  }
}
