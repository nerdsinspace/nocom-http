package com.matt.nocom.server.service.auth;

import com.matt.nocom.server.model.sql.auth.User;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthenticationProvider implements UserDetailsService {
  private final UserRepository login;
  
  public Optional<User> findUser(final String username) {
    return login.getUser(username);
  }
  
  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    return findUser(username).orElseThrow(() -> new UsernameNotFoundException(username));
  }
}
