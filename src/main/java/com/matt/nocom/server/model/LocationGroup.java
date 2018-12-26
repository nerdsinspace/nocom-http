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

  public LocationGroup organize() {
    setX(getPositions().stream().collect(Collectors.averagingInt(Position::getX)).intValue());
    setZ(getPositions().stream().collect(Collectors.averagingInt(Position::getZ)).intValue());
    return this;
  }

  public int distanceTo(LocationGroup other) {
    return (int) Math.ceil(Math.sqrt(Math.pow(other.getX() - getX(), 2) + Math.pow(other.getZ() - getZ(), 2)));
  }

  @JsonIgnore
  public boolean isInGroup(LocationGroup other, int distance) {
    return distanceTo(other) <= distance;
  }

  @JsonIgnore
  public boolean isInSameWorld(LocationGroup other) {
    return getServer().equalsIgnoreCase(other.getServer()) && getDimension() == other.getDimension();
  }
}
