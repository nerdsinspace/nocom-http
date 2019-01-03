package com.matt.nocom.server.util.factory;

import com.google.common.collect.Lists;
import com.matt.nocom.server.model.Location;
import com.matt.nocom.server.model.LocationGroup;
import com.matt.nocom.server.model.Position;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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

        List<Position> positions = null;
        Iterator<LocationGroup> it = locations.iterator();
        while(it.hasNext()) {
          LocationGroup loc = it.next();
          if(head.isInSameWorld(loc) && head.isInGroup(loc, rng)) {
            it.remove();
            if(positions == null) positions = Lists.newArrayList();
            positions.addAll(loc.getPositions());
          }
        }

        if(positions != null && !positions.isEmpty()) {
          positions.addAll(head.getPositions());
          positions.sort(Comparator.comparingLong(Position::getTime));

          head.setPositions(positions);
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
