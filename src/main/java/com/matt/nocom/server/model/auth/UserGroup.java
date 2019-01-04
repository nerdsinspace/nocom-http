package com.matt.nocom.server.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public enum UserGroup implements GrantedAuthority {
  ADMIN(0),
  USER(4),
  ;

  private int level;

  public String getName() {
    return name();
  }

  @JsonIgnore
  @Override
  public String getAuthority() {
    return getName();
  }
}
