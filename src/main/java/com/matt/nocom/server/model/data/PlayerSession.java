package com.matt.nocom.server.model.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerSession {
  private String username;
  private UUID uuid;
  private Instant join;
  private Instant leave;
}
