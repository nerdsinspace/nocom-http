package com.matt.nocom.server.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.matt.nocom.server.util.StaticUtils;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

import lombok.*;
import lombok.Builder.Default;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements UserDetails {
  private Integer id;
  private String username;
  private String password; // encrypted
  private String passwordPlaintext;
  private boolean enabled;
  private int level;
  private UserGroup group = UserGroup.UNPRIVILEGED;
  private boolean debugUser;
  private Instant lastLogin;

  @Builder
  public User(Integer id,
      String username, String password, String passwordPlaintext,
      boolean enabled,
      Integer level, UserGroup group,
      boolean debugUser,
      Instant lastLogin) {
    setId(id);
    setUsername(username);
    setPassword(password);
    setPasswordPlaintext(passwordPlaintext);
    setEnabled(enabled);
    setLevel(level);
    setGroup(group);
    setDebugUser(debugUser);
    setLastLogin(lastLogin);
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

  public UserGroup getGroup() {
    return group;
  }

  @JsonIgnore
  public boolean isNotDebugUser() {
    return !isDebugUser();
  }

  @JsonIgnore
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(getGroup());
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public String toString() {
    return username + ", authorities=[" + getGroup().name() + "]";
  }

  public static class UserBuilder {
    public UserBuilder group(UserGroup group) {
      return level(group.getLevel());
    }
  }
}
