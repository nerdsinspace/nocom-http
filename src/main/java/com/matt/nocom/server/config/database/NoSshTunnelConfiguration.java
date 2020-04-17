package com.matt.nocom.server.config.database;

import org.apache.sshd.client.session.forward.PortForwardingTracker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(prefix = "nocom.ssh-tunnel", name = "enabled",
    havingValue = "false", matchIfMissing = true)
public class NoSshTunnelConfiguration {
  @Bean
  @ConditionalOnMissingBean(value = PortForwardingTracker.class, name = "sshPortTunnel")
  public Object sshPortTunnel() {
    return null;
  }
}
