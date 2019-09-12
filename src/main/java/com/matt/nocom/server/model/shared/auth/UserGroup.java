package com.matt.nocom.server.model.shared.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matt.nocom.server.Properties;
import java.util.EnumSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public enum UserGroup implements GrantedAuthority {
  ROOT(Properties.USER_LEVEL_MAX),
  ADMIN(90),
  DEV(50),
  UNPRIVILEGED(Properties.USER_LEVEL_MIN)
  ;

  private int level;

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
  
  public static EnumSet<UserGroup> all() {
    return ALL;
  }
  
  public static EnumSet<UserGroup> privileged() {
    return PRIVILEGED;
  }
  
  public static EnumSet<UserGroup> admins() {
    return ADMINS;
  }
  
  public static UserGroup fromLevel(int level) {
    UserGroup closest = UNPRIVILEGED;
    for (UserGroup group : all()) {
      if (group.getLevel() == level) {
        return group;
      } else if (group.getLevel() < level && group.getLevel() > closest.getLevel()) {
        closest = group; // find the lowest possible group
      }
    }
    return closest;
  }
}
