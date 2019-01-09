package com.matt.nocom.server.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
  private boolean enabled;

  @Singular
  private Set<UserGroup> groups;

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

  public UsernamePasswordAuthenticationToken toUsernamePasswordAuthenticationToken() {
    return new UsernamePasswordAuthenticationToken(getUsername(), getPassword(), getAuthorities());
  }

  @Override
  public String toString() {
    return username
        + ", authorities=["
        + getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", "))
        + "]";
  }
}
