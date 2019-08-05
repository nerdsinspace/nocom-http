package com.matt.nocom.server.listeners.auth;

import com.matt.nocom.server.service.EventService;
import com.matt.nocom.server.util.EventTypeRegistry;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

public class UserLogoutSuccessfulHandler extends SimpleUrlLogoutSuccessHandler {
  private final EventService events;

  public UserLogoutSuccessfulHandler(EventService events) {
    this.events = events;

    setDefaultTargetUrl("/login?logout");
    setAlwaysUseDefaultTargetUrl(true);
  }

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    events.publishInfo(authentication, EventTypeRegistry.LOGOUT, "Logged out");
    super.onLogoutSuccess(request, response, authentication);
  }
}
