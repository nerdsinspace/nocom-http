package com.matt.nocom.server.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matt.nocom.server.Properties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public enum UserGroup implements GrantedAuthority {
  DEBUG(0),
  ADMIN(0),
  USER(4),
  ;

  private int level;

  public String getName() {
    return name();
  }

  public boolean isAllowed() {
    return this != DEBUG || Properties.DEBUG_AUTH;
  }

  @JsonIgnore
  @Override
  public String getAuthority() {
    return getName();
  }
}
