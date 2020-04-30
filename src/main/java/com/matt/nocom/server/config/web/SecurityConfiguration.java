package com.matt.nocom.server.config.web;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import com.matt.nocom.server.Logging;
import com.matt.nocom.server.model.auth.UserGroup;
import com.matt.nocom.server.service.auth.*;
import com.matt.nocom.server.service.web.AccessDeniedEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter implements Logging {
  private final String[] publicUris;
  private final String[] adminOnlyUris;
  private final CORSFilter corsFilter;
//  private final JWTAuthenticationFilter jwtAuthenticationFilter;
  private final JWTAuthorizationFilter jwtAuthorizationFilter;
  private final UserAuthenticationProvider authProvider;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authProvider);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public DefaultAuthenticationEventPublisher authenticationEventPublisher() {
    return new DefaultAuthenticationEventPublisher();
  }

  @Bean
  public String[] allAuthorities() {
    return UserGroup.privileged().stream()
        .map(UserGroup::getName)
        .toArray(String[]::new);
  }

  @Bean
  public String[] adminAuthorities() {
    return UserGroup.admins().stream()
        .map(UserGroup::getName)
        .toArray(String[]::new);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    var src = new UrlBasedCorsConfigurationSource();
    var cfg = new CorsConfiguration().applyPermitDefaultValues();
    cfg.setAllowCredentials(true);
    cfg.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:4200/", "localhost:4200", "localhost"));
    cfg.setAllowedMethods(Arrays.stream(HttpMethod.values())
        .map(Enum::name)
        .collect(Collectors.toList()));
    src.registerCorsConfiguration("/**", cfg);
    return src;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    JwtWebSecurityConfigurer
        .forRS256("https://localhost/", "https://localhost/", authProvider)
        .configure(http)

        .headers()

        // allows iframes to work
        .frameOptions().disable()
        .cacheControl().disable()

        .and().cors()

        .configurationSource(corsConfigurationSource())

        .and().authorizeRequests()

        .antMatchers(publicUris).permitAll()
        .antMatchers(adminOnlyUris).hasAnyAuthority(adminAuthorities())
        .anyRequest().hasAnyAuthority(allAuthorities())

        .and()

//        .addFilter(jwtAuthenticationFilter)
        .addFilterBefore(jwtAuthorizationFilter, BasicAuthenticationFilter.class);
  }
}
