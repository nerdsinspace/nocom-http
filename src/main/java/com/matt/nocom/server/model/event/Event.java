package com.matt.nocom.server.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
  private int id;
  private Instant createdTime;
  private String type;
  private String causedBy;
  private String message;
}
