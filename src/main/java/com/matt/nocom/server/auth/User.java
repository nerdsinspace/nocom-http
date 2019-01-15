package com.matt.nocom.server.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matt.nocom.server.Properties;
import com.matt.nocom.server.util.Util;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
  private String username;
  private String password; // encrypted

  @Default
  private boolean enabled = true;

  @Singular
  private Set<UserGroup> groups;

  @JsonIgnore
  public boolean isNotDebugUser() {
    return Properties.DEBUG_AUTH || !getGroups().contains(UserGroup.DEBUG);
  }

  @JsonIgnore
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return getGroups();
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
    return username
        + ", authorities=["
        + getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", "))
        + "]";
  }
}
