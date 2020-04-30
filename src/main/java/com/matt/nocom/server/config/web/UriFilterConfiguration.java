package com.matt.nocom.server.config.web;

import com.matt.nocom.server.util.StaticUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

@Configuration
@Order(1)
public class UriFilterConfiguration {
  @Bean
  public String[] publicUris() {
    return new String[]{
        "/",
        "/login**",
        "/404",
        "/public/**",
        "/user/login",
        "/js/**",
        "/webjars/**",
        "/css/**",
        "/fonts/**",
        "/img/**"
    };
  }

  @Bean
  public String[] adminOnlyUris() {
    return new String[]{
        "/user/**",
        "/accounts",
        "/events**",
        "/api/database/download"
    };
  }

  @Bean
  public List<RequestMatcher> restApiMatchers() {
    return StaticUtils.antMatchers("/api/**", "/user/**");
  }
}
