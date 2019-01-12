package com.matt.nocom.server.config;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.auth.UserGroup;
import com.matt.nocom.server.service.LoginManagerService;
import com.matt.nocom.server.util.AuthenticationTokenFilter;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
      "/manager"
  };

  private final LoginManagerService login;

  public SecurityConfiguration(LoginManagerService login) {
    this.login = login;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(login)
        .passwordEncoder(passwordEncoder());
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  private static String[] allAuthorities() {
    return Arrays.stream(UserGroup.values())
        .filter(UserGroup::isAllowed)
        .map(UserGroup::getName)
        .toArray(String[]::new);
  }

  private static String[] adminAuthorities() {
    return Stream.of(UserGroup.ADMIN, UserGroup.DEBUG)
        .filter(UserGroup::isAllowed)
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
        .formLogin()
        .loginPage("/login")
          .defaultSuccessUrl("/overview")
          .permitAll()
        .and()
        .logout()
          .invalidateHttpSession(true)
          .clearAuthentication(true)
          .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
          .logoutSuccessUrl("/login?logout")
          .permitAll()
        .and()
        .exceptionHandling()
          .accessDeniedHandler(((request, response, accessDeniedException) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(auth != null) LOGGER.warn(
                "{} attempted to access forbidden resource at {} with authorities [{}]",
                auth.getName(),
                request.getRequestURI(),
                auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(", ")));
            response.sendRedirect(request.getContextPath() + "/access-denied");
          }))
          .authenticationEntryPoint(((request, response, authException) -> response.sendError(403, "Access denied")))
        .and()
        .csrf().disable()
        .addFilterBefore(new AuthenticationTokenFilter(login, authenticationManager()), BasicAuthenticationFilter.class);
  }
}
