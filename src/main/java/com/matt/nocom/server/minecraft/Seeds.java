package com.matt.nocom.server.minecraft;

//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Collectors;

public class Seeds {
  private static final Map<String, Long> SEED_MAP = new HashMap<>();
  static {
    /*JsonObject json = new JsonParser().parse(new InputStreamReader(Seeds.class.getResourceAsStream("seeds.json"))).getAsJsonObject();

    SEED_MAP.putAll(json.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getAsLong()))
    );*/
    SEED_MAP.put("2b2t.org", -4172144997902289642L);
    SEED_MAP.put("constantiam.net", 7540332306713543803L);
  }

  public static Optional<Long> forServer(String ip) {
    return Optional.ofNullable(SEED_MAP.get(ip));
  }


}
