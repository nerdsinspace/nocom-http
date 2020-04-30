package com.matt.nocom.server.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.matt.nocom.server.config.auth.JWTConfiguration;
import com.matt.nocom.server.model.auth.AuthenticatedUser;
import com.matt.nocom.server.model.auth.JwtToken;
import com.matt.nocom.server.model.auth.User;
import com.matt.nocom.server.properties.JWTProperties;
import com.matt.nocom.server.service.EventRepository;
import com.matt.nocom.server.service.auth.UserAuthenticationProvider;
import com.matt.nocom.server.service.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.Instant;

import static com.matt.nocom.server.Logging.getLogger;

@RestController
@RequiredArgsConstructor
public class LoginController {
  private final AuthenticationManager authenticationManager;
  private final JWTProperties properties;
  private final JWTConfiguration jwt;
  private final EventRepository events;
  private final UserRepository users;

  @PostMapping("/login")
  public AuthenticatedUser login(@RequestParam String username, @RequestParam String password,
      HttpServletResponse response) {
    var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    var user = (User) auth.getPrincipal();

    var token = JwtToken.builder()
        .username(user.getUsername())
        .expiresAt(jwt.newExpirationDate())
        .level(user.getLevel())
        .build()
        .encode()
        .sign(jwt.signature());

    getLogger().debug("Created new JWT token for {} \"{}\"", auth.getName(), token);

    response.addHeader(properties.getHeaderName(), properties.getTokenPrefixWithSpace() + token);

    events.publishUser(auth, "/login", "Logged in");
    users.setUserLastLogin(user.getUsername(), Instant.now());

    return AuthenticatedUser.builder()
        .username(user.getUsername())
        .level(user.getLevel())
        .token(token)
        .build();
  }

  @PostMapping("/validate")
  public AuthenticatedUser validate(Authentication authentication) {
    var user = (User) authentication.getPrincipal();
    var token = (String) authentication.getCredentials();
    return AuthenticatedUser.builder()
        .username(user.getUsername())
        .level(user.getLevel())
        .token(token)
        .build();
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid credentials")
  public void handleException(AuthenticationException e) { }

  @ExceptionHandler({JWTVerificationException.class, UsernameNotFoundException.class})
  @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Invalid token")
  public void handleVerificationException(JWTVerificationException e) { }
}
