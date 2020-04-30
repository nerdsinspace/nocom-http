package com.matt.nocom.server.service.auth;

import com.google.common.base.MoreObjects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Component
public class CORSFilter implements Filter {
  private CorsConfigurationSource cors;

  @Autowired
  public void setCors(CorsConfigurationSource corsConfigurationSource) {
    this.cors = corsConfigurationSource;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if(response instanceof HttpServletResponse) {
      var res = (HttpServletResponse) response;
      var config = cors.getCorsConfiguration((HttpServletRequest) request);
      Objects.requireNonNull(config, "config is null");

      res.addHeader("Access-Control-Allow-Methods",
          String.join(", ", MoreObjects.firstNonNull(config.getAllowedMethods(), Collections.emptyList())));
      res.addHeader("Access-Control-Allow-Headers",
          String.join(", ", MoreObjects.firstNonNull(config.getAllowedHeaders(), Collections.emptyList())));
//      res.addHeader("Access-Control-Allow-Credentials",
//          Optional.ofNullable(config.getAllowCredentials())
//              .map(v -> v ? "true" : "false")
//              .orElse("false"));
    }

    chain.doFilter(request, response);
  }
}
