package com.matt.nocom.server.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.net.InetAddress;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessToken {
  private UUID token;
  private InetAddress address;
  private long expiresOn;

  @JsonIgnore
  public boolean isExpired() {
    return System.currentTimeMillis() >= expiresOn;
  }
}
