package com.matt.nocom.server.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
  private int id;
  private long time;
  private EventLevel level;
  private EventType type;
  private int userId;
  private String username;
  private String message;
}
