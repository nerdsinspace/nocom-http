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
    List<LocationGroup> locations = input.stream()
        .map(loc -> LocationGroup.builder()
            .server(loc.getServer())
            .dimension(loc.getDimension())
            .position(loc.getPosition())
            .build().organize())
        .collect(Collectors.toList());

    if(range > 0) {
      boolean changed;
      do {
        changed = false;
        List<LocationGroup> improved = Lists.newArrayList();
        while (!locations.isEmpty()) {
          final LocationGroup head = locations.remove(0);

          List<LocationGroup> neighbors = locations.stream()
              .filter(head::isInSameWorld)
              .filter(loc -> loc.isInGroup(head, range))
              .collect(Collectors.toList());

          if (!neighbors.isEmpty()) {
            List<Position> merged = neighbors.stream()
                .peek(locations::remove) // remove from locations list
                .map(LocationGroup::getPositions)
                .flatMap(List::stream)
                .collect(Collectors.toList()); // merge all positions into head
            merged.addAll(head.getPositions());
            merged.sort(Comparator.comparingLong(Position::getTime));

            improved.add(LocationGroup.builder()
                .server(head.getServer())
                .dimension(head.getDimension())
                .positions(merged)
                .build().organize());

            changed = true;
          } else
            improved.add(head);
        }
        locations.addAll(improved);
      } while (changed);
    }

    return locations;
  }
}
