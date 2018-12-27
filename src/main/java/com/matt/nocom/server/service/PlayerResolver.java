package com.matt.nocom.server.service;

import com.google.common.base.MoreObjects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.matt.nocom.server.model.MojangPlayer;
import com.matt.nocom.server.model.Player;
import com.matt.nocom.server.sqlite.tables.Players;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PlayerResolver {
  private Cache<UUID, String> cache = CacheBuilder.newBuilder()
      .expireAfterWrite(24, TimeUnit.HOURS)
      .build();

  public Player getOrResolve(Player player) {
    String name = cache.getIfPresent(player.getUuid());
    if(name == null) {
      Player pl = resolve(new RestTemplate(), player);
      cache.put(pl.getUuid(), pl.getName());
      return pl;
    } else
      return Player.builder()
          .uuid(player.getUuid())
          .name(name)
          .build();
  }

  private Player resolve(RestTemplate rest, Player player) {
    ResponseEntity<MojangPlayer[]> response = rest.exchange(
        getQueryUri(player.getUuid()),
        HttpMethod.GET,
        new HttpEntity<>(new HttpHeaders()),
        MojangPlayer[].class
    );

    if(response.getStatusCode().isError())
      throw new Error("GET " + player.getUuid().toString() + ": failed with status code " + response.getStatusCode());

    Objects.requireNonNull(response.getBody(), "GET " + player.getUuid().toString() + ": missing body");

    return Player.builder()
        .uuid(player.getUuid())
        .name(Stream.of(response.getBody())
            .max(Comparator.comparingLong(pl -> MoreObjects.firstNonNull(pl.getChangedToAt(), Long.MAX_VALUE)))
            .map(MojangPlayer::getName)
            .orElseThrow(() -> new Error("GET " + player.getUuid().toString() + ": no name history provided")))
        .build();
  }

  private static String getQueryUri(UUID uuid) {
    return "https://api.mojang.com/user/profiles/"
        + uuid.toString().replaceAll("-", "")
        + "/names";
  }
}
