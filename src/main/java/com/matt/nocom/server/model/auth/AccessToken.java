package com.matt.nocom.server.model.auth;

import java.net.InetAddress;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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

  @Nullable
  @Default
  private User user = null;

  public boolean isExpired() {
    return System.currentTimeMillis() >= expiresOn;
  }
}
