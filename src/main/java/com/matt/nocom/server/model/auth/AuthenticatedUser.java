package com.matt.nocom.server.model.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

@Getter
public class AuthenticatedUser implements UserDetails {
  private final String username;
  private final String token;
  private int level;
  private UserGroup group;

  @Builder
  public AuthenticatedUser(@NonNull String username, Integer level, UserGroup group, String token) {
    this.username = username;
    this.token = token;

    if(level == null) {
      setGroup(Objects.requireNonNull(group, "must provide UserGroup if no level provided"));
    } else {
      setLevel(Objects.requireNonNull(level, "must provide user level or group"));
    }
  }

  public void setLevel(int level) {
    this.level = level;
    this.group = UserGroup.byLevel(level);
  }

  public void setGroup(UserGroup group) {
    if(group != null) {
      this.group = group;
      this.level = group.getLevel();
    }
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(getGroup());
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }
}
