package com.matt.nocom.server.service.auth;

import com.auth0.jwt.JWT;
import com.matt.nocom.server.config.auth.JWTConfiguration;
import com.matt.nocom.server.model.auth.JwtToken;
import com.matt.nocom.server.model.auth.User;
import com.matt.nocom.server.properties.AuthenticationProperties;
import com.matt.nocom.server.properties.JWTProperties;
import com.matt.nocom.server.service.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

import static com.matt.nocom.server.Logging.getLogger;

//@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final JWTProperties properties;
  private final JWTConfiguration jwt;
  private final EventRepository events;
  private final UserRepository users;

  @Autowired
  @Override
  public void setAuthenticationManager(AuthenticationManager authenticationManager) {
    super.setAuthenticationManager(authenticationManager);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException {
    var username = Objects.requireNonNull(request.getParameter(getUsernameParameter()), "missing username");
    var password = Objects.requireNonNull(request.getParameter(getPasswordParameter()), "missing password");

    return getAuthenticationManager()
        .authenticate(new UsernamePasswordAuthenticationToken(username, password));
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult)
      throws IOException, ServletException {
    if(authResult.getPrincipal() instanceof User) {
      var user = (User) authResult.getPrincipal();

      var token = JwtToken.builder()
          .username(user.getUsername())
          .expiresAt(jwt.newExpirationDate())
          .level(user.getLevel())
          .build()
          .encode()
          .sign(jwt.signature());

      getLogger().debug("Created new JWT token for {} \"{}\"", authResult.getName(), token);

      response.addHeader(properties.getHeaderName(), properties.getTokenPrefixWithSpace() + token);

      events.publishUser(authResult, "/login", "Logged in");
      users.setUserLastLogin(user.getUsername(), Instant.now());
    }
  }
}
