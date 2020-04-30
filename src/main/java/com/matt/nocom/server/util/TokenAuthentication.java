package com.matt.nocom.server.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.UUID;

public class TokenAuthentication extends UsernamePasswordAuthenticationToken {
  public TokenAuthentication(UUID token) {
    super(token, token);
  }
}
