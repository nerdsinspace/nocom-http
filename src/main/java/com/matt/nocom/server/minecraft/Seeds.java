package com.matt.nocom.server.minecraft;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Seeds {
  private static final Map<String, Long> SEED_MAP = new HashMap<>();
  static {
    SEED_MAP.put("2b2t.org", -4172144997902289642L);
    SEED_MAP.put("constantiam.net", 7540332306713543803L);
  }

  public static Optional<Long> forServer(String ip) {

    return Optional.ofNullable(SEED_MAP.get(ip));
  }


}
