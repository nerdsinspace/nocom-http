package com.matt.nocom.server.util.factory;

import com.google.common.collect.Lists;
import com.matt.nocom.server.model.Location;
import com.matt.nocom.server.model.LocationGroup;
import com.matt.nocom.server.model.Position;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LocationGroupFactory {
  public static List<LocationGroup> translate(List<Location> input, int range) {
    final int rng = range*range;

    List<LocationGroup> locations = input.stream()
        .map(loc -> LocationGroup.builder()
            .server(loc.getServer())
            .dimension(loc.getDimension())
            .position(loc.getPosition())
            .build().setup())
        .collect(Collectors.toList());

    boolean changed;
    do {
      changed = false;
      List<LocationGroup> improved = Lists.newArrayList();
      while (!locations.isEmpty()) {
        final LocationGroup head = locations.remove(0);

        List<LocationGroup> neighbors = locations.stream()
            .filter(head::isInSameWorld)
            .filter(loc -> loc.isInGroup(head, rng))
            .collect(Collectors.toList());

        if (!neighbors.isEmpty()) {
          List<Position> merged = neighbors.stream()
              .peek(locations::remove) // remove from locations list
              .map(LocationGroup::getPositions)
              .flatMap(List::stream)
              .collect(Collectors.toList()); // merge all positions into head
          merged.addAll(head.getPositions());
          merged.sort(Comparator.comparingLong(Position::getTime));

          head.setPositions(merged);
          head.setup();

          changed = true;
        }

        improved.add(head);
      }
      locations.addAll(improved);
    } while (changed);

    return locations;
  }
}
