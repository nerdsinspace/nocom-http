package com.matt.nocom.server.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessToken {
  public static AccessToken newAccessToken(@NonNull InetAddress address) {
    return new AccessToken(UUID.randomUUID(), address, Instant.now(), null);
  }

  private final UUID token;
  private final InetAddress address;
  private final Instant createdOn;
  private final Duration lifespan;

  @JsonIgnore
  public boolean isExpired() {
    return Instant.now().toEpochMilli() > getCreatedOn().minus(getLifespan()).toEpochMilli();
  }
}
