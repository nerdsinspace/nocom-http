package com.matt.nocom.server.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties("nocom.ssh-tunnel")
public class SshTunnelProperties {
  private boolean enabled;
  private String hostAddress;
  private int hostPort = 22;
  private String username;
  private String privateKeyFile;
  private String localHost = "localhost";
  private int localPort = 5432;
  private String remoteHost = "localhost";
  private int remotePort = 5432;
  private Duration timeout = Duration.ofSeconds(5);
}
