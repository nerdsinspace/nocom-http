package com.matt.nocom.server.config;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.Properties;
import com.matt.nocom.server.service.LoginAccessDeniedHandler;
import com.matt.nocom.server.service.LoginService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@ComponentScan({"com.matt.nocom.server.service"})
public class SecurityConfiguration extends WebSecurityConfigurerAdapter implements Logging {
  private final LoginService login;
  private final LoginAccessDeniedHandler accessDeniedHandler;

  public SecurityConfiguration(LoginService login,
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
    if(Properties.DEBUG_AUTH) {
      LOGGER.warn("Debug authentication loaded");
      auth
          .inMemoryAuthentication()
          .withUser("root").password(passwordEncoder().encode("pass")).roles("ADMIN");
    } else
      auth
          .userDetailsService(login)
          .passwordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
          .antMatchers("/",
              "/js/**",
              "/css/**",
              "/fonts/**",
              "/img/**").permitAll()
          .anyRequest().authenticated()
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
        .csrf().disable();
  }
}
