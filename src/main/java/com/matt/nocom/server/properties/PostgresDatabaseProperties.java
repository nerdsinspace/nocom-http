package com.matt.nocom.server.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("nocom.datasource.postgres")
public class PostgresDatabaseProperties {
  private String hostname = "localhost";
  private int port = 5432;
  private String database;
  private String username;
  private String password;
}
