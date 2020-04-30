package com.matt.nocom.server.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties("nocom.jwt")
public class JWTProperties {
  private int keyLength = 2048;
  private Duration tokenLifespan = Duration.ofDays(1);
  private String tokenPrefix = "Bearer";
  private String headerName = "Authorization";

  public String getTokenPrefixWithSpace() {
    return getTokenPrefix() + " ";
  }

  public String parseHttpHeader(String header) {
    return header.substring(getTokenPrefixWithSpace().length());
  }
}
