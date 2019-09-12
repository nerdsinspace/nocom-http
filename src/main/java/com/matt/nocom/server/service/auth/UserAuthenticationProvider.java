package com.matt.nocom.server.service.auth;

import com.matt.nocom.server.model.sql.auth.User;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticationProvider implements UserDetailsService {
  
  private final LoginService login;
  
  public UserAuthenticationProvider(LoginService login) {
    this.login = login;
  }
  
  public Optional<User> findUser(final String username) {
    return login.getUser(username);
  }
  
  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    return findUser(username).orElseThrow(() -> new UsernameNotFoundException(username));
  }
}
