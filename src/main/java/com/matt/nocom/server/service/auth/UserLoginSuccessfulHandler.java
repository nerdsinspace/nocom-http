package com.matt.nocom.server.service.auth;

import com.matt.nocom.server.service.EventRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserLoginSuccessfulHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final EventRepository events;
  private final UserRepository login;

  {
    setDefaultTargetUrl("/overview");
    setAlwaysUseDefaultTargetUrl(false);
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    events.publishUser(authentication, "/login", "Logged in via web");
  
    Optional.ofNullable(authentication)
        .map(Authentication::getName)
        .ifPresent(username -> login.setUserLastLogin(username, Instant.now()));

    super.onAuthenticationSuccess(request, response, authentication);
  }
}
