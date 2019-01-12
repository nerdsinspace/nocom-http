package com.matt.nocom.server.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matt.nocom.server.Properties;
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
  public static User nameOnly(String username) {
    return User.builder()
        .username(username)
        .build();
  }

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
