package com.matt.nocom.server.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.matt.nocom.server.config.auth.JWTConfiguration;
import com.matt.nocom.server.model.auth.JwtToken;
import com.matt.nocom.server.model.auth.User;
import com.matt.nocom.server.properties.JWTProperties;
import com.matt.nocom.server.service.EventRepository;
import com.matt.nocom.server.service.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
  public String login(@RequestParam String username, @RequestParam String password,
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

    // token contains all the encoded details
    return token;
  }

  @PostMapping("/validate")
  public ResponseEntity<Object> validate() {
    // the user must have a valid session token in order to reach this method
    // otherwise they will get an authentication error
    return ResponseEntity.ok().build();
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid credentials")
  public void handleException(AuthenticationException e) {
  }

  @ExceptionHandler({JWTVerificationException.class, UsernameNotFoundException.class})
  @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Invalid token")
  public void handleVerificationException(JWTVerificationException e) {
  }
}
