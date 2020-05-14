package com.matt.nocom.server.service.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.matt.nocom.server.model.data.ClusterNode;
import com.matt.nocom.server.model.data.Hit;
import com.matt.nocom.server.model.data.SimpleHit;
import com.matt.nocom.server.model.data.TrackedHits;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Component
public class NocomUtility {
  public List<TrackedHits> groupByTrackId(List<Hit> hits) {
    Map<Integer, TrackedHits> tracks = Maps.newLinkedHashMap();

    for(Hit hit : hits) {
      var parent = tracks.computeIfAbsent(hit.getTrackId(), tid -> TrackedHits.builder()
          .trackId(tid)
          .dimension(hit.getDimension())
          .hits(Lists.newArrayList())
          .build());

      parent.addHit(hit);
    }

    return Lists.newArrayList(tracks.values());
  }

  public List<SimpleHit> aggregateHits(List<SimpleHit> hits, Duration aggregationDuration) {
    if(hits.size() < 4) {
      return hits;
    }

    long totalMs = 0;
    int firstIndex = 0;
    for(int i = 0; i < hits.size(); i++) {
      SimpleHit current = hits.get(i);

      // if the current index isn't the first index (we need 2 points to make a line)
      // and has previous
      if(i != firstIndex && i - 1 >= 0) {
        totalMs += hits.get(i - 1).getCreatedAt().toEpochMilli()
            - current.getCreatedAt().toEpochMilli();
      }

      // if this is the last index
      // or total ms is greater than the aggregation duration
      if(i == hits.size() - 1
          || totalMs > aggregationDuration.toMillis()) {
        // create the new weighted hit
        SimpleHit weightedHit = averageOfHits(hits.subList(firstIndex, i + 1));
        // save the current index
        int endIndex = i;
        // reset our index back to firstIndex
        i = firstIndex;
        // set object at this index to our new weighted hit
        hits.set(i, weightedHit);
        // remove all the indexes until the end index
        for(int j = i + 1; j < Math.min(endIndex + 1, hits.size()); j++) {
          hits.remove(j);
        }
        // the new first index is the next index
        firstIndex = i + 1;
        // reset total ms
        totalMs = 0;
      }
    }

    return hits;
  }

  private SimpleHit averageOfHits(Collection<SimpleHit> hits) {
    return SimpleHit.builder()
        .x((int) Math.round(hits.stream()
            .mapToInt(SimpleHit::getX)
            .average()
            .orElseThrow()))
        .z((int) Math.round(hits.stream()
            .mapToInt(SimpleHit::getZ)
            .average()
            .orElseThrow()))
        .createdAt(hits.stream()
            .map(SimpleHit::getCreatedAt)
            .min(Instant::compareTo)
            .orElseThrow())
        .build();
  }

  public ClusterNode compressClusters(Collection<ClusterNode> nodes) {
    // find the root node
    var root = nodes.stream()
        .filter(n -> n.getClusterParent() == null)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Could not find the root node for the cluster"));

    // remove the root node
    nodes.remove(root);

    List<ClusterNode.Leaf> leafs = Lists.newArrayList();
    root.setLeafs(leafs);

    for(var next : nodes) {
      leafs.add(new ClusterNode.Leaf(next.getId(), next.getX(), next.getZ()));
    }

    return root;
  }
}
