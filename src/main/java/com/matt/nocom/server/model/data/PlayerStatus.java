package com.matt.nocom.server.model.data;

import com.matt.nocom.server.postgres.codegen.enums.StatusesEnum;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PlayerStatus {
  private String playerUsername;
  private UUID playerUuid;
  private String server;
  private StatusesEnum state;
  private Instant updatedAt;
  private String data;
  private Dimension dimension;
}
