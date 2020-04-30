package com.matt.nocom.server.controller;

import com.matt.nocom.server.model.data.SimpleHit;
import com.matt.nocom.server.model.data.TrackedHits;
import com.matt.nocom.server.service.data.NocomRepository;
import com.matt.nocom.server.service.data.NocomUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NocomApiController {
  private final NocomRepository nocom;
  private final NocomUtility util;

  @RequestMapping(value = "/track-history",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public List<SimpleHit> trackHistory(@RequestParam int trackId,
      @RequestParam(defaultValue = "1000") long max,
      @RequestParam(defaultValue = "2000") long aggregationMs) {
    return util.aggregateHits(nocom.getTrackHistory(trackId, limitOf(max)), Duration.ofMillis(aggregationMs));
  }

  @RequestMapping(value = "/full-track-history",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
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

  private static long limitOf(long max) {
    return max < 1 ? Long.MAX_VALUE : max;
  }
}
