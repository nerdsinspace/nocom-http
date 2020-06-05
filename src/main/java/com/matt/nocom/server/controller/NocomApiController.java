package com.matt.nocom.server.controller;

import com.matt.nocom.server.model.data.*;
import com.matt.nocom.server.service.data.NocomRepository;
import com.matt.nocom.server.service.data.NocomUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NocomApiController {
  private final NocomRepository nocom;
  private final NocomUtility util;

  @PostMapping("/track-history")
  @ResponseBody
  public List<SimpleHit> trackHistory(@RequestParam int trackId,
      @RequestParam(defaultValue = "1000") long max,
      @RequestParam(defaultValue = "2000") long aggregationMs) {
    return util.aggregateHits(nocom.getTrackHistory(trackId, limitOf(max)), Duration.ofMillis(aggregationMs));
  }

  @PostMapping("/full-track-history")
  @ResponseBody
  public Collection<TrackedHits> fullTrackHistory(@RequestParam int trackId,
      @RequestParam(defaultValue = "1000") long max,
      @RequestParam(defaultValue = "2000") long aggregationMs) {
    var hits = util.groupByTrackId(nocom.getFullTrackHistory(trackId, limitOf(max)));
    var agg = Duration.ofMillis(aggregationMs);

    for(TrackedHits tracked : hits) {
      util.aggregateHits(tracked.getHits(), agg);
    }

    return hits;
  }

  @PostMapping("/root-clusters")
  @ResponseBody
  public Collection<ClusterNode> rootClusters(@RequestParam String server, @RequestParam int dimension) {
    return nocom.getRootClusters(server, Dimension.byOrdinal(dimension));
  }

  @PostMapping("/cluster")
  @ResponseBody
  public ClusterNode clusters(@RequestParam int clusterId) {
    return util.compressClusters(nocom.getFullCluster(clusterId));
  }

  @PostMapping("/cluster-associations")
  @ResponseBody
  public List<Player> clusterAssociations(@RequestParam int clusterId) {
    return nocom.getClusterPlayerAssociations(clusterId);
  }
  
  @GetMapping("/bot-statuses")
  @ResponseBody
  public List<PlayerStatus> botStatuses() {
    return nocom.getBotStatuses();
  }

  @PostMapping("/player-sessions")
  @ResponseBody
  public List<SessionGroup> playerSessions(
      @RequestParam UUID[] playerUuids,
      @RequestParam(required = false) String server,
      @RequestParam(required = false) Optional<Long> from,
      @RequestParam(required = false) Optional<Long> to,
      @RequestParam(defaultValue = "1000") int max) {
    return util.groupSessions(nocom.getPlayerSessions(server,
        from.map(Instant::ofEpochMilli).orElse(null),
        to.map(Instant::ofEpochMilli).orElse(null),
        max, playerUuids));
  }

  private static long limitOf(long max) {
    return max < 1 ? Long.MAX_VALUE : max;
  }
}
