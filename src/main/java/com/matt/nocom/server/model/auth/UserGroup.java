package com.matt.nocom.server.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.EnumSet;
import java.util.Map;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public enum UserGroup implements GrantedAuthority {
  ROOT(100),
  ADMIN(90),
  DEV(50),
  UNPRIVILEGED(0)
  ;

  private final int level;

  public String getName() {
    return name();
  }
  
  @Override
  public String toString() {
    return getName() + "(" + getLevel() + ")";
  }
  
  @JsonIgnore
  @Override
  public String getAuthority() {
    return getName();
  }
  
  private static final EnumSet<UserGroup> ALL = EnumSet.allOf(UserGroup.class);
  private static final EnumSet<UserGroup> PRIVILEGED = EnumSet.of(ROOT, ADMIN, DEV);
  private static final EnumSet<UserGroup> ADMINS = EnumSet.of(ROOT, ADMIN);
  private static final Map<Integer, UserGroup> LEVEL_CACHE = Maps.newHashMap();
  
  public static EnumSet<UserGroup> all() {
    return ALL;
  }
  
  public static EnumSet<UserGroup> privileged() {
    return PRIVILEGED;
  }
  
  public static EnumSet<UserGroup> admins() {
    return ADMINS;
  }
  
  public static UserGroup byLevel(int level) {
    var g = LEVEL_CACHE.get(level);
    if(g == null) {
      g = UNPRIVILEGED;
      for (UserGroup group : all()) {
        if (group.getLevel() == level) {
          return group;
        } else if (group.getLevel() < level && group.getLevel() > g.getLevel()) {
          g = group; // find the lowest possible group
        }
      }
      LEVEL_CACHE.put(level, g);
    }
    return g;
  }
}
