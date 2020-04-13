package com.matt.nocom.server.model.sql.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.net.InetAddress;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.UUID;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class AccessToken {
  public static AccessToken newAccessToken(@NonNull InetAddress address) {
    return new AccessToken(UUID.randomUUID(), address, null);
  }

  private final UUID token;
  private final InetAddress address;
  private final Instant createdOn;

  @JsonIgnore
  public boolean isExpired(TemporalAmount lifespan) {
    return Instant.now().minus(lifespan).compareTo(createdOn) > 0;
  }
}
