package com.matt.nocom.server.config.profiles;

import com.matt.nocom.server.Logging;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("database.properties")
@Profile("dev")
public class DeveloperConfiguration implements Logging {
  private final Environment env;
  
  @Autowired
  public DeveloperConfiguration(Environment env) {
    this.env = env;
  }
  
  @Bean
  public Path sqliteDatabasePath() {
    String fn = env.getRequiredProperty("db.file");
    String dev = fn + ".dev";
    
    Path path = Paths.get(fn);
    if(Files.exists(path)) {
      try {
        Files.copy(path, Paths.get(dev), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        LOGGER.error(e, e);
      }
    }
    return Paths.get(dev);
  }
}
