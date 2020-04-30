package com.matt.nocom.server.model.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtToken {
  private final String username;
  private final Date expiresAt;
  private final int level;

  public UserGroup getGroupFromLevel() {
    return UserGroup.byLevel(level);
  }

  public JWTCreator.Builder encode() {
    return JWT.create()
        .withSubject(getUsername())
        .withExpiresAt(getExpiresAt())
        .withClaim("lvl", getLevel());
  }

  public static JwtToken decode(DecodedJWT jwt) {
    return JwtToken.builder()
        .username(jwt.getSubject())
        .expiresAt(jwt.getExpiresAt())
        .level(jwt.getClaim("lvl").asInt())
        .build();
  }
}
