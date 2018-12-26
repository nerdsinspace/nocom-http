package com.matt.nocom.server.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Player {
  private UUID uuid;

  @Override
  public boolean equals(Object obj) {
    return this == obj || (obj instanceof Player && getUuid().equals(((Player) obj).getUuid()));
  }

  @Override
  public int hashCode() {
    return getUuid().hashCode();
  }
}
