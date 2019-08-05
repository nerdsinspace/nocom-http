package com.matt.nocom.server.listeners.auth;

import com.matt.nocom.server.service.EventService;
import com.matt.nocom.server.util.EventTypeRegistry;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class UserLoginSuccessfulHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final EventService events;

  public UserLoginSuccessfulHandler(EventService events) {
    this.events = events;

    setDefaultTargetUrl("/overview");
    setAlwaysUseDefaultTargetUrl(false);
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    events.publishInfo(authentication, EventTypeRegistry.LOGIN, "Logged in via web");

    super.onAuthenticationSuccess(request, response, authentication);
  }
}
