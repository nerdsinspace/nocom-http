package com.matt.nocom.server.service.auth;

import com.auth0.jwt.JWT;
import com.matt.nocom.server.config.auth.JWTConfiguration;
import com.matt.nocom.server.model.auth.JwtToken;
import com.matt.nocom.server.properties.JWTProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTAuthorizationFilter extends GenericFilterBean {
  private final JWTProperties properties;
  private final JWTConfiguration jwt;
  private final UserAuthenticationProvider authenticationProvider;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    // try and get a http parameter (for websockets)
    var authorization = request.getParameter("accessToken");

    // if that doesn't work, try to get the authorization header
    if (authorization == null && request instanceof HttpServletRequest) {
      var header = ((HttpServletRequest) request).getHeader(properties.getHeaderName());
      // check if token has Bearer prefix
      if (header != null && header.startsWith(properties.getTokenPrefix())) {
        authorization = properties.parseHttpHeader(header);
      }
    }

    if (authorization != null) {
      var encoded = JWT.require(jwt.signature())
          .build()
          .verify(authorization);
      var data = JwtToken.decode(encoded);
      var user = authenticationProvider.loadUserByUsername(data.getUsername());

      SecurityContextHolder.getContext().setAuthentication(
          new UsernamePasswordAuthenticationToken(user, authorization, user.getAuthorities())
      );
    }

    chain.doFilter(request, response);
  }
}
