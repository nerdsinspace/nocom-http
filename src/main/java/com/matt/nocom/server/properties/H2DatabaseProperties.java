package com.matt.nocom.server.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("nocom.datasource.h2")
public class H2DatabaseProperties {
  private String mode;
  private String database;
  private String username;
  private String password;
}
