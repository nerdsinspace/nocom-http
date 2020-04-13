package com.matt.nocom.server.config.database;

import com.matt.nocom.server.properties.SshTunnelProperties;
import lombok.RequiredArgsConstructor;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.session.forward.PortForwardingTracker;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.common.config.keys.loader.openssh.OpenSSHKeyPairResourceParser;
import org.apache.sshd.common.config.keys.loader.pem.PEMResourceParserUtils;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;

@Configuration
@EnableConfigurationProperties(SshTunnelProperties.class)
@ConditionalOnProperty(prefix = "nocom.ssh-tunnel", name = "enabled")
@RequiredArgsConstructor
public class SshTunnelConfiguration {
  private final SshTunnelProperties properties;

  @Bean(destroyMethod = "stop")
  public SshClient sshClient() {
    var key = Paths.get(properties.getPrivateKeyFile());
    if (!Files.exists(key)) {
      throw new IllegalStateException("Could not find private key file \""
          + key.toAbsolutePath() + "\"");
    } else if (!Files.isRegularFile(key)) {
      throw new IllegalStateException("Private key file is not a file (possibly a directory?)");
    }

    SshClient client = SshClient.setUpDefaultClient();

    var provider = new FileKeyPairProvider(key);
    client.setKeyIdentityProvider(provider);
    client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);

    for(KeyPair kp : provider.loadKeys(null)) {
      client.addPublicKeyIdentity(kp);
    }

    client.start();
    return client;
  }

  @Bean(destroyMethod = "close")
  public ClientSession sshSession() throws IOException {
    var session = sshClient().connect(properties.getUsername(), properties.getHostAddress(), properties.getHostPort())
        .verify(properties.getTimeout().toMillis())
        .getClientSession();
    session.auth().verify(properties.getTimeout().toMillis());
    return session;
  }

  @Bean(destroyMethod = "close")
  public PortForwardingTracker databasePortForwarder() throws IOException {
    var local = new SshdSocketAddress(properties.getLocalHost(), properties.getLocalPort());
    var remote = new SshdSocketAddress(properties.getRemoteHost(), properties.getRemotePort());
    return sshSession().createLocalPortForwardingTracker(local, remote);
  }
}
