package com.matt.nocom.server.config.auth;

import com.auth0.jwt.algorithms.Algorithm;
import com.matt.nocom.server.Logging;
import com.matt.nocom.server.h2.codegen.Tables;
import com.matt.nocom.server.h2.codegen.tables.records.JwtSignkeypairRecord;
import com.matt.nocom.server.properties.AuthenticationProperties;
import com.matt.nocom.server.properties.JWTProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Date;

import static com.matt.nocom.server.Logging.*;
import static com.matt.nocom.server.h2.codegen.Tables.*;

@Configuration
@RequiredArgsConstructor
public class JWTConfiguration {
  private final JWTProperties properties;
  private final DSLContext dsl;

  public Date newExpirationDate() {
    return Date.from(Instant.now().plus(properties.getTokenLifespan()));
  }

  @Bean
  public Algorithm signature() {
    return Algorithm.RSA512((RSAPublicKey) getKeyPair().getPublic(), (RSAPrivateKey) getKeyPair().getPrivate());
  }

  @Bean
  @SneakyThrows
  public KeyFactory keyAlgorithm() {
    return KeyFactory.getInstance("RSA");
  }

  @Bean
  public KeyPair getKeyPair() {
    return dsl.selectFrom(JWT_SIGNKEYPAIR)
        .orderBy(JWT_SIGNKEYPAIR.CREATED_ON.desc())
        .limit(1)
        .fetchOptional()
        .map(this::convertToKeyPair)
        .orElseGet(this::generateKeyPair);

  }

  @SneakyThrows
  private KeyPair convertToKeyPair(JwtSignkeypairRecord record) {
    return new KeyPair(
        keyAlgorithm().generatePublic(new X509EncodedKeySpec(record.getPublicKey())),
        keyAlgorithm().generatePrivate(new PKCS8EncodedKeySpec(record.getPrivateKey()))
    );
  }

  @SneakyThrows
  private KeyPair generateKeyPair() {
    getLogger().debug("Generating new RSA key with modulus of {}", properties.getKeyLength());

    var alg = KeyPairGenerator.getInstance(keyAlgorithm().getAlgorithm());
    alg.initialize(properties.getKeyLength());
    var kp = alg.generateKeyPair();

    dsl.insertInto(JWT_SIGNKEYPAIR)
        .columns(JWT_SIGNKEYPAIR.PUBLIC_KEY, JWT_SIGNKEYPAIR.PRIVATE_KEY)
        .values(kp.getPublic().getEncoded(), kp.getPrivate().getEncoded())
        .execute();

    return kp;
  }
}
