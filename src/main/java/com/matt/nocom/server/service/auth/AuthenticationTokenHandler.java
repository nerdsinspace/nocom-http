package com.matt.nocom.server.service.auth;

import com.matt.nocom.server.util.StaticUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
@RequiredArgsConstructor
public class AuthenticationTokenHandler extends GenericFilterBean {
  private static final String AUTHORIZATION_IDENTIFIER = "Authorization";
  
  private final UserRepository login;
  private final UserAuthenticationProvider authProvider;
  
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String provided = findAuthorization(request);

    if(provided != null) {
      login.getUsernameByToken(UUID.fromString(provided), InetAddress.getByName(request.getRemoteAddr()))
          .map(authProvider::loadUserByUsername)
          .filter(UserDetails::isEnabled)
          .filter(UserDetails::isAccountNonExpired)
          .filter(UserDetails::isAccountNonLocked)
          .filter(UserDetails::isCredentialsNonExpired)
          .map(StaticUtils::toAuthenticationToken)
          .ifPresent(SecurityContextHolder.getContext()::setAuthentication);
    }

    chain.doFilter(request, response);
  }

  @Nullable
  private static String findAuthorization(ServletRequest request) {
    return Optional.of(request)
        .filter(HttpServletRequest.class::isInstance)
        .map(HttpServletRequest.class::cast)
        .map(req -> req.getHeader(AUTHORIZATION_IDENTIFIER))
        .orElse(request.getParameter(AUTHORIZATION_IDENTIFIER));
  }
}
