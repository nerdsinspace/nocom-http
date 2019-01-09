package com.matt.nocom.server.config;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.model.auth.UserGroup;
import com.matt.nocom.server.service.LoginAccessDeniedHandler;
import com.matt.nocom.server.service.LoginManagerService;
import com.matt.nocom.server.util.AuthenticationTokenFilter;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@ComponentScan({"com.matt.nocom.server.service"})
public class SecurityConfiguration extends WebSecurityConfigurerAdapter implements Logging {
  private final LoginManagerService login;
  private final LoginAccessDeniedHandler accessDeniedHandler;

  public SecurityConfiguration(LoginManagerService login,
      LoginAccessDeniedHandler accessDeniedHandler) {
    this.login = login;
    this.accessDeniedHandler = accessDeniedHandler;
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

  private String[] allowedAuthorities() {
    return Arrays.stream(UserGroup.values())
        .filter(UserGroup::isAllowed)
        .map(UserGroup::getName)
        .toArray(String[]::new);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
          .antMatchers("/",
              "/api/authenticate",
              "/js/**",
              "/css/**",
              "/fonts/**",
              "/img/**").permitAll()
          .anyRequest().hasAnyAuthority(allowedAuthorities())
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
        .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
        .and()
        .csrf().disable()
        .addFilterBefore(new AuthenticationTokenFilter(login), BasicAuthenticationFilter.class);
  }
}
