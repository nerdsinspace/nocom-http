package com.matt.nocom.server.util.factory;

import com.google.common.collect.Lists;
import com.matt.nocom.server.model.sql.data.Location;
import com.matt.nocom.server.model.http.data.LocationGroup;
import com.matt.nocom.server.model.sql.data.Position;
import com.matt.nocom.server.util.kdtree.KdNode;
import com.matt.nocom.server.util.kdtree.KdTree;
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
            .position(loc.toPosition())
            .build())
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

  public static List<LocationGroup> translate2(List<Location> inputs, int range) {
    if(inputs.isEmpty())
      return Collections.emptyList();

    final int rangeSq = range*range;

    // sort the results by server and dimension
    inputs.sort(Comparator.comparing(Location::getServer, String.CASE_INSENSITIVE_ORDER)
        .thenComparingInt(Location::getDimension));

    List<KdTree<Location>> trees = Lists.newArrayList();

    List<Location> list = Lists.newArrayList();
    for(int i = 0; i < inputs.size(); i++) {
      Location loc = inputs.get(i);
      list.add(loc);

      Location next = (i + 1) < inputs.size() ? inputs.get(i + 1) : null;
      if((next != null && !next.isInSameWorld(loc)) || i == (inputs.size() - 1)) {
        trees.add(new KdTree<>(list));
        list = Lists.newArrayList();
      }
    }

    List<LocationGroup> joined = Lists.newArrayListWithCapacity(inputs.size());

    for(KdTree<Location> tree : trees) {
      final Location first = tree.getRoot().getReference();

      while(!tree.isEmpty()) {
        List<KdNode<Location>> nodes;
        if(rangeSq <= 0)
          nodes = Collections.singletonList(tree.getRoot());
        else
          nodes = tree.radiusSq(tree.getRoot(), rangeSq);

        if(nodes.isEmpty()) {
          tree.remove(tree.getRoot());
          continue;
        }

        LocationGroup lg = LocationGroup.builder()
            .server(first.getServer())
            .dimension(first.getDimension())
            .positions(nodes.stream()
                .map(KdNode::getReferences)
                .flatMap(List::stream)
                .map(Location::toPosition)
                .sorted(Comparator.comparingLong(Position::getTime))
                .collect(Collectors.toList()))
            .build();

          nodes.forEach(tree::remove);

          joined.add(lg);
      }
    }

    return joined;
  }
}
