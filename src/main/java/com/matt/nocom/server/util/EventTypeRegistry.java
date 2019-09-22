package com.matt.nocom.server.util;

import com.google.common.collect.Lists;
import com.matt.nocom.server.Logging;
import com.matt.nocom.server.model.sql.event.EventType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EventTypeRegistry implements Logging {
  private static final List<EventType> REGISTRY = Lists.newArrayList();

  public static void register(EventType type) {
    REGISTRY.add(type);
  }

  public static EventType create(int id, String name, byte[] hash) {
    EventType type = new EventType(id, name, hash);
    register(type);
    return type;
  }

  public static EventType create(String name) {
    EventType type = new EventType(name);
    register(type);
    return type;
  }

  public static Collection<EventType> all() {
    return Collections.unmodifiableCollection(REGISTRY);
  }

  public static Optional<EventType> getByHash(byte[] hash) {
    Objects.requireNonNull(hash);
    for(EventType type : REGISTRY) {
      if (Arrays.equals(hash, type.getHash()))
        return Optional.of(type);
    }
    return Optional.empty();
  }

  // system
  public static final EventType READY                               = create("Application.Ready");

  // /api/
  public static final EventType API__ADD_LOCATION                   = create("/api/upload/locations.Locations");
  public static final EventType API__ADD_LOCATION__SERVERS          = create("/api/upload/locations.Server");
  public static final EventType API__ADD_LOCATION__POSITIONS        = create("/api/upload/locations.Positions");
  public static final EventType API__DOWNLOAD_DATABASE              = create("/api/database/download");

  // /user/
  public static final EventType USER__LOGIN                         = create("/user/login");
  public static final EventType USER__LOGIN__CREATE_TOKEN           = create("/user/login.CreateToken");
  public static final EventType USER__REGISTER                      = create("/user/register");
  public static final EventType USER__UNREGISTER                    = create("/user/unregister");
  public static final EventType USER__EXPIRE_TOKENS                 = create("/user/tokens/user/{username}/expire");
  public static final EventType USER__EXPIRE_ONE_TOKEN              = create("/user/tokens/expire/{uuid}");
  public static final EventType USER__SET_ENABLE                    = create("/user/set/enabled/{username}");
  public static final EventType USER__SET_PASSWORD = create("/user/set/password/{username}");
  public static final EventType USER__SET_LEVEL = create("/user/set/level/{username}");

  // /
  public static final EventType LOGIN                               = create("/login");
  public static final EventType LOGOUT                              = create("/logout");
  public static final EventType ACCESS_DENIED                       = create("/access-denied");
}
