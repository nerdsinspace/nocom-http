package com.matt.nocom.server.util;

import com.matt.nocom.server.service.auth.LoginService;
import com.matt.nocom.server.service.auth.UserAuthenticationProvider;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.GenericFilterBean;

public class AuthenticationTokenFilter extends GenericFilterBean {
  private static final String AUTHORIZATION_IDENTIFIER = "Authorization";
  
  private final LoginService login;
  private final UserAuthenticationProvider authProvider;
  private final AuthenticationManager authentication;
  
  public AuthenticationTokenFilter(LoginService login,
      UserAuthenticationProvider authProvider,
      AuthenticationManager authentication) {
    this.login = login;
    this.authProvider = authProvider;
    this.authentication = authentication;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String provided = findAuthorization(request);

    if(provided != null) {
      login.getUsernameByToken(UUID.fromString(provided), Util.getRemoteAddr(request))
          .map(authProvider::loadUserByUsername)
          .filter(UserDetails::isEnabled)
          .filter(UserDetails::isAccountNonExpired)
          .filter(UserDetails::isAccountNonLocked)
          .filter(UserDetails::isCredentialsNonExpired)
          .map(Util::toAuthenticationToken)
          .ifPresent(auth -> SecurityContextHolder.getContext().setAuthentication(auth));
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
