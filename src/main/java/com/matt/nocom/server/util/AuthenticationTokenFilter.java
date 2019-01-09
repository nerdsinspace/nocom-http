package com.matt.nocom.server.util;

import com.google.common.base.MoreObjects;
import com.matt.nocom.server.model.auth.User;
import com.matt.nocom.server.service.LoginManagerService;
import com.matt.nocom.server.util.Util;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class AuthenticationTokenFilter extends GenericFilterBean {
  private final LoginManagerService login;

  public AuthenticationTokenFilter(LoginManagerService login) {
    this.login = login;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = Optional.of(request)
        .filter(HttpServletRequest.class::isInstance)
        .map(HttpServletRequest.class::cast)
        .orElseThrow(() -> new Error("request is not instance of HttpServletRequest"));

    String provided = req.getHeader("Access-Token");
    if(provided == null) provided = req.getParameter("access-token");

    if(provided != null) {
      login.getUserByToken(UUID.fromString(provided), Util.stringToAddress(req.getRemoteAddr()))
          .ifPresent(user -> SecurityContextHolder.getContext()
              .setAuthentication(user.toUsernamePasswordAuthenticationToken()));
    }

    chain.doFilter(request, response);
  }
}
