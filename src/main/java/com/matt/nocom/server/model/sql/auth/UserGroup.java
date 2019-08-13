package com.matt.nocom.server.model.sql.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matt.nocom.server.Properties;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public enum UserGroup implements GrantedAuthority {
  ADMIN(0, false),
  DEV(4, false),
  DEBUG(0, true),
  ;

  private int level;
  private boolean debug;

  public String getName() {
    return name();
  }

  public boolean isHighestPrivileges() {
    return getLevel() == 0;
  }

  public boolean isActive() {
    return Properties.DEBUG_AUTH || !isDebug();
  }

  @JsonIgnore
  @Override
  public String getAuthority() {
    return getName();
  }

  private static final EnumSet<UserGroup> ACTIVE = Arrays.stream(values())
      .filter(UserGroup::isActive)
      .collect(Collectors.toCollection(() -> EnumSet.noneOf(UserGroup.class)));

  private static final EnumSet<UserGroup> HIGHEST_PRIVILEGES = Arrays.stream(values())
      .filter(UserGroup::isHighestPrivileges)
      .collect(Collectors.toCollection(() -> EnumSet.noneOf(UserGroup.class)));

  private static final EnumSet<UserGroup> PRODUCTION = Arrays.stream(values())
      .filter(g -> !g.isDebug())
      .collect(Collectors.toCollection(() -> EnumSet.noneOf(UserGroup.class)));

  public static EnumSet<UserGroup> active() {
    return ACTIVE;
  }

  public static EnumSet<UserGroup> highestPrivileges() {
    return HIGHEST_PRIVILEGES;
  }

  public static EnumSet<UserGroup> production() {
    return PRODUCTION;
  }
}
