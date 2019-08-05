package com.matt.nocom.server.listeners.auth;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.service.EventService;
import com.matt.nocom.server.util.EventLevelRegistry;
import com.matt.nocom.server.util.EventTypeRegistry;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

public class UserAccessDeniedHandler implements AccessDeniedHandler, Logging {
  private final EventService events;

  public UserAccessDeniedHandler(EventService events) {
    this.events = events;
  }

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if(auth != null) {
      events.publish(EventLevelRegistry.WARN, EventTypeRegistry.ACCESS_DENIED, auth, "Attempt to access resource '%s' with insufficient privileges (%s)",
          request.getRequestURI(),
          auth.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.joining(", ")));
    }
    response.sendRedirect(request.getContextPath() + "/access-denied");
  }
}
