package com.matt.nocom.server.service.auth;

import com.matt.nocom.server.model.auth.AuthenticatedUser;
import com.matt.nocom.server.model.auth.User;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthenticationProvider extends DaoAuthenticationProvider
    implements UserDetailsService, UserDetailsPasswordService {
  private final UserRepository users;

  {
    setUserDetailsService(this);
  }

  @Autowired
  @Override
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    super.setPasswordEncoder(passwordEncoder);
  }

  public Optional<User> findUser(final String username) {
    return users.getUser(username);
  }
  
  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    return findUser(username).orElseThrow(() -> new UsernameNotFoundException(username));
  }

  @Override
  public UserDetails updatePassword(UserDetails user, String newPassword) {
    if(users.setUserEncodedPassword(user.getUsername(), newPassword) > 0) {
      return loadUserByUsername(user.getUsername());
    } else {
      throw new Error("Unable to update password");
    }
  }
}
