package com.matt.nocom.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AccessLevel;
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
public class GroupedLocation implements Serializable {
  private long newestTime;
  private long oldestTime;
  private int centerX;
  private int centerZ;
  private String server;
  private int dimension;
  private List<Position> positions;

  public void setPositions(List<Position> positions) {
    this.positions = positions;
    evaluate();
  }

  private void evaluate() {
    Objects.requireNonNull(getPositions(), "position list");

    setNewestTime(getPositions().stream()
        .map(Position::getTime)
        .max(Comparator.comparingLong(Long::longValue))
        .orElse(getNewestTime()));
    setOldestTime(getPositions().stream()
        .map(Position::getTime)
        .min(Comparator.comparingLong(Long::longValue))
        .orElse(getOldestTime()));

    setCenterX(getPositions().stream()
        .collect(Collectors.averagingInt(Position::getX)).intValue());
    setCenterZ(getPositions().stream()
        .collect(Collectors.averagingInt(Position::getZ)).intValue());
  }

  public int distanceTo(GroupedLocation other) {
    return (int) Math.ceil(Math.sqrt(Math.pow(other.getCenterX() - getCenterX(), 2) + Math.pow(other.getCenterZ() - getCenterZ(), 2)));
  }

  public boolean isInGroup(GroupedLocation other, int distance) {
    return distanceTo(other) <= distance;
  }

  public boolean isInSameWorld(GroupedLocation other) {
    return getServer().equalsIgnoreCase(other.getServer()) && getDimension() == other.getDimension();
  }

  public static class GroupedLocationBuilder {
    private List<Position> positions = Lists.newArrayList();

    public GroupedLocationBuilder position(Position position) {
      return positions(Collections.singleton(position));
    }

    public GroupedLocationBuilder positions(Collection<Position> positions) {
      this.positions.addAll(positions);
      this.positions.sort(Comparator.comparingLong(Position::getTime).reversed());
      return evaluate();
    }

    private GroupedLocationBuilder evaluate() {
      this.newestTime = this.positions.stream()
          .map(Position::getTime)
          .max(Comparator.comparingLong(Long::longValue))
          .orElse(0L);
      this.oldestTime = this.positions.stream()
          .map(Position::getTime)
          .min(Comparator.comparingLong(Long::longValue))
          .orElse(0L);

      this.centerX = this.positions.stream()
          .collect(Collectors.averagingInt(Position::getX)).intValue();
      this.centerZ = this.positions.stream()
          .collect(Collectors.averagingInt(Position::getZ)).intValue();

      return this;
    }
  }
}
