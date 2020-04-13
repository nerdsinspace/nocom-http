package com.matt.nocom.server.config.web;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.model.shared.auth.UserGroup;
import com.matt.nocom.server.service.auth.*;
import com.matt.nocom.server.service.web.AccessDeniedEntryPoint;
import com.matt.nocom.server.util.StaticUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter implements Logging {
  private final String[] publicUris;
  private final String[] adminOnlyUris;
  private final PasswordEncoder passwordEncoder;
  private final UserAuthenticationProvider authProvider;
  private final AuthenticationTokenHandler authenticationTokenHandler;
  private final UserAccessDeniedHandler userAccessDeniedHandler;
  private final UserLoginSuccessfulHandler userLoginSuccessfulHandler;
  private final UserLogoutSuccessfulHandler userLogoutSuccessfulHandler;
  private final AccessDeniedEntryPoint accessDeniedEntryPoint;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(authProvider).passwordEncoder(passwordEncoder);
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

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()

        .antMatchers(publicUris).permitAll()
        .antMatchers(adminOnlyUris).hasAnyAuthority(adminAuthorities())
        .anyRequest().hasAnyAuthority(allAuthorities())

        .and().headers()

        // allows iframes to work
        .frameOptions().disable()
        .cacheControl().disable()

        .and().formLogin()

        .loginPage("/login")
        .successHandler(userLoginSuccessfulHandler)
        .permitAll()

        .and().logout()

        .invalidateHttpSession(true)
        .clearAuthentication(true)
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        .logoutSuccessHandler(userLogoutSuccessfulHandler)
        .permitAll()

        .and().exceptionHandling()

        .accessDeniedHandler(userAccessDeniedHandler)
        .authenticationEntryPoint(accessDeniedEntryPoint)

        .and().csrf().disable()

        .addFilterBefore(authenticationTokenHandler, BasicAuthenticationFilter.class);
  }
}
