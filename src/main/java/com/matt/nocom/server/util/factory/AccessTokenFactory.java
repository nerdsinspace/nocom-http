package com.matt.nocom.server.util.factory;

import com.google.common.collect.Lists;
import com.matt.nocom.server.Properties;
import com.matt.nocom.server.auth.AccessToken;
import com.matt.nocom.server.util.Util;
import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

public class AccessTokenFactory {
  public static AccessToken generate(InetAddress address) {
    return AccessToken.builder()
        .token(UUID.randomUUID())
        .expiresOn(System.currentTimeMillis() + Properties.TOKEN_EXPIRATION)
        .address(address)
        .build();
  }

  public static List<AccessToken> fromConcatStrings(String[] tokens, String[] addresses, String[] expires) {
    if(Util.arraysSameLength(tokens, addresses, expires))
      throw new IllegalArgumentException("concat strings are of different lengths");

    List<AccessToken> accessTokens = Lists.newArrayList();
    for(int i = 0; i < tokens.length; ++i) {
      accessTokens.add(AccessToken.builder()
          .token(UUID.fromString(tokens[i]))
          .address(Util.stringToAddress(addresses[i]))
          .expiresOn(Long.valueOf(expires[i]))
          .build());
    }
    return accessTokens;
  }
}
