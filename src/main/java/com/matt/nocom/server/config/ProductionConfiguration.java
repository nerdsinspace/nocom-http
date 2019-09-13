package com.matt.nocom.server.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("database.properties")
@Profile("!dev")
public class ProductionConfiguration {
  private final Environment env;
  
  @Autowired
  public ProductionConfiguration(Environment env) {
    this.env = env;
  }
  
  @Bean
  public Path sqliteDatabasePath() {
    return Paths.get(env.getRequiredProperty("db.file"));
  }
}
