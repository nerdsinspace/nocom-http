package com.matt.nocom.server.service.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccessDeniedEntryPoint implements AuthenticationEntryPoint {
  private final List<RequestMatcher> restApiMatchers;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
    if(restApiMatchers.stream().anyMatch(matcher -> matcher.matches(request))) {
      response.sendError(403, "Access denied");
    } else {
      response.sendRedirect("/");
    }
  }
}
