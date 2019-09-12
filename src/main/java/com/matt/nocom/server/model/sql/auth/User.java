package com.matt.nocom.server.model.sql.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matt.nocom.server.model.shared.auth.UserGroup;
import com.matt.nocom.server.util.Util;
import java.util.Collection;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
  @Default
  private int id = -1;

  private String username;
  private String password; // encrypted
  private String passwordPlaintext;

  @Default
  private boolean enabled = true;
  
  @Default
  private int level = 0;
  
  @Default
  private boolean debugUser = false;
  
  @Default
  private long lastLogin = 0L;
  
  public UserGroup getGroup() {
    return UserGroup.fromLevel(getLevel());
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

  public UsernamePasswordAuthenticationToken toAuthenticationToken() {
    return Util.toAuthenticationToken(this);
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
