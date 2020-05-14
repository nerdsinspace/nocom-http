package com.matt.nocom.server.model.data;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Player {
  private String username;
  private UUID uuid;
  private double strength;
}
