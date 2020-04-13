package com.matt.nocom.server.service.auth;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.service.EventRepository;

import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAccessDeniedHandler implements AccessDeniedHandler, Logging {
  private final EventRepository events;

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if(auth != null) {
      events.publishUser(auth, "AccessDenied",
          "Attempt to access resource '%s' with insufficient privileges (%s)",
          request.getRequestURI(),
          auth.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.joining(", ")));
    }
    response.sendRedirect(request.getContextPath() + "/access-denied");
  }
}
