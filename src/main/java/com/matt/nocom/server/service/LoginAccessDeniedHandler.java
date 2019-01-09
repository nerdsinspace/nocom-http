package com.matt.nocom.server.service;

import com.matt.nocom.server.Logging;
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
import org.springframework.stereotype.Component;

@Component
public class LoginAccessDeniedHandler implements Logging, AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if(auth != null) LOGGER.warn("{} attempted to access forbidden resource at {} with authorities [{}]",
        auth.getName(),
        request.getRequestURI(),
        auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(", ")));
    response.sendRedirect(request.getContextPath() + "/access-denied");
  }
}
