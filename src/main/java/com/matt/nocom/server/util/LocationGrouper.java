package com.matt.nocom.server.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.matt.nocom.server.model.GroupedLocation;
import com.matt.nocom.server.model.Location;
import com.matt.nocom.server.model.Position;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class LocationGrouper {
  private final List<GroupedLocation> groupedLocations;

  public LocationGrouper(List<Location> list, int groupingRange) {
    Queue<GroupedLocation> locations = list.stream()
        .map(loc -> GroupedLocation.builder()
            .server(loc.getServer())
            .dimension(loc.getDimension())
            .position(loc.getPosition())
            .build())
        .collect(Queues::newArrayDeque, Queue::add, Queue::addAll);

    for(; improve(locations, groupingRange););

    this.groupedLocations = Lists.newArrayList(locations);
  }

  private boolean improve(Queue<GroupedLocation> locations, final int range) {
    boolean changed = false;
    Queue<GroupedLocation> improved = Queues.newArrayDeque();
    while(!locations.isEmpty()) {
      final GroupedLocation head = locations.poll();

      List<GroupedLocation> neighbors = locations.stream()
          .filter(head::isInSameWorld)
          .filter(loc -> loc.isInGroup(head, range))
          .collect(Collectors.toList());

      if(!neighbors.isEmpty()) {
        List<Position> merged = neighbors.stream()
            .peek(locations::remove) // remove from locations list
            .map(GroupedLocation::getPositions)
            .flatMap(List::stream)
            .collect(Collectors.toList()); // merge all positions into head
        improved.add(GroupedLocation.builder()
            .server(head.getServer())
            .dimension(head.getDimension())
            .positions(head.getPositions())
            .positions(merged)
            .build());

        changed = true;
      }
      else improved.add(head);
    }
    locations.addAll(improved);
    return changed;
  }

  public List<GroupedLocation> getGroupedLocations() {
    return groupedLocations;
  }
}
