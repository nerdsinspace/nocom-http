package com.matt.nocom.server.controller;

import com.matt.nocom.server.model.data.QueryTracks;
import com.matt.nocom.server.service.data.NocomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Controller;

import java.time.Duration;
import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class WebsocketController {
  private final NocomRepository nocom;
  private final SimpMessageSendingOperations messenger;

  @MessageMapping("/tracking")
  public void tracks(QueryTracks track, @Header("simpSessionId") String sessionId) {
    var payload = nocom.getMostRecentTracks(track.getServer(),
        Instant.ofEpochMilli(track.getTime()),
        Duration.ofMillis(track.getDuration()));
    messenger.convertAndSendToUser(sessionId, "/ws-subscribe/tracker", payload, createHeaders(sessionId));
  }

  private MessageHeaders createHeaders(String sessionId) {
    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
    headerAccessor.setSessionId(sessionId);
    headerAccessor.setLeaveMutable(true);
    return headerAccessor.getMessageHeaders();
  }
}
