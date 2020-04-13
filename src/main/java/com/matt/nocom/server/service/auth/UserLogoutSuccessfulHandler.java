package com.matt.nocom.server.service.auth;

import com.matt.nocom.server.service.EventRepository;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserLogoutSuccessfulHandler extends SimpleUrlLogoutSuccessHandler {
  private final EventRepository events;

  {
    setDefaultTargetUrl("/login?logout");
    setAlwaysUseDefaultTargetUrl(true);
  }

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    events.publishUser(authentication, "/logout", "Logged out");

    super.onLogoutSuccess(request, response, authentication);
  }
}
