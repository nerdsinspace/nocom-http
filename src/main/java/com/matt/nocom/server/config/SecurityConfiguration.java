package com.matt.nocom.server.config;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.auth.UserGroup;
import com.matt.nocom.server.listeners.auth.UserAccessDeniedHandler;
import com.matt.nocom.server.listeners.auth.UserLoginSuccessfulHandler;
import com.matt.nocom.server.service.EventService;
import com.matt.nocom.server.service.LoginManagerService;
import com.matt.nocom.server.listeners.auth.UserLogoutSuccessfulHandler;
import com.matt.nocom.server.util.AuthenticationTokenFilter;
import com.matt.nocom.server.util.Util;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@ComponentScan({"com.matt.nocom.server.service"})
public class SecurityConfiguration extends WebSecurityConfigurerAdapter implements Logging {
  private static final String[] PUBLIC_URIS = new String[] {
      "/",
      "/user/login",
      "/js/**",
      "/css/**",
      "/fonts/**",
      "/img/**"
  };
  private static final String[] ADMIN_ONLY_URIS = new String[] {
      "/user/**",
      "/manager",
      "/api/database/download"
  };

  private static final List<RequestMatcher> REST_API_MATCHERS = Util.antMatchers("/api/**", "/user/**");

  private final PasswordEncoder passwordEncoder;
  private final LoginManagerService login;
  private final EventService events;

  @Autowired
  public SecurityConfiguration(LoginManagerService login, PasswordEncoder passwordEncoder,
      EventService events) {
    this.login = login;
    this.passwordEncoder = passwordEncoder;
    this.events = events;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(login)
        .passwordEncoder(passwordEncoder);
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
  public AuthenticationSuccessHandler successHandler() {
    return new UserLoginSuccessfulHandler(events);
  }

  @Bean
  public LogoutSuccessHandler logoutSuccessHandler() {
    return new UserLogoutSuccessfulHandler(events);
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return new UserAccessDeniedHandler(events);
  }

  private static String[] allAuthorities() {
    return UserGroup.active().stream()
        .map(UserGroup::getName)
        .toArray(String[]::new);
  }

  private static String[] adminAuthorities() {
    return UserGroup.highestPrivileges().stream()
        .map(UserGroup::getName)
        .toArray(String[]::new);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
          .antMatchers(PUBLIC_URIS).permitAll()
          .antMatchers(ADMIN_ONLY_URIS).hasAnyAuthority(adminAuthorities())
          .anyRequest().hasAnyAuthority(allAuthorities())
        .and()
          .headers()
            .frameOptions().disable() // allows iframes to work
        .and()
          .formLogin()
          .loginPage("/login")
              .successHandler(successHandler())
              .permitAll()
        .and()
          .logout()
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessHandler(logoutSuccessHandler())
            .permitAll()
        .and()
        .exceptionHandling()
          .accessDeniedHandler(accessDeniedHandler())
          .authenticationEntryPoint(((request, response, authException) -> {
            if(REST_API_MATCHERS.stream().anyMatch(matcher -> matcher.matches(request)))
              response.sendError(403, "Access denied");
            else
              response.sendRedirect("/");
          }))
        .and()
          .csrf().disable()
          .addFilterBefore(new AuthenticationTokenFilter(login, authenticationManager()), BasicAuthenticationFilter.class);
  }
}
