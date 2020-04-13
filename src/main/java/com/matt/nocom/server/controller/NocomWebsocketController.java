package com.matt.nocom.server.controller;

import com.matt.nocom.server.model.http.data.UpdateTrack;
import com.matt.nocom.server.model.sql.data.Track;
import com.matt.nocom.server.service.data.NocomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NocomWebsocketController {
  private final NocomRepository nocom;

  @MessageMapping("/tracking")
  @SendTo("/nocom/subscribe/tracker")
  public List<Track> tracks(UpdateTrack update) {
    return nocom.getMostRecentTracks(update.getServer(), Duration.ofMillis(update.getDuration()));
  }
}
