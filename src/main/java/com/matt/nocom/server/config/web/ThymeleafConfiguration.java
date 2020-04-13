package com.matt.nocom.server.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

@Configuration
public class ThymeleafConfiguration {
  @Bean
  public SpringSecurityDialect springSecurityDialect() {
    return new SpringSecurityDialect();
  }
}
