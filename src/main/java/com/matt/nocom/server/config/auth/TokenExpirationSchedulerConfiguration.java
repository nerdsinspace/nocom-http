package com.matt.nocom.server.config.auth;

import com.matt.nocom.server.properties.AuthenticationProperties;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import com.matt.nocom.server.service.auth.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@ComponentScan({"com.matt.nocom.server.service"})
@EnableScheduling
public class TokenExpirationSchedulerConfiguration implements SchedulingConfigurer {

  private final AuthenticationProperties properties;
  private final UserRepository login;
  
  public TokenExpirationSchedulerConfiguration(AuthenticationProperties properties, UserRepository login) {
    this.properties = properties;
    this.login = login;
  }
  
  @Bean(destroyMethod = "shutdown")
  public ExecutorService taskExecutor() {
    return Executors.newSingleThreadScheduledExecutor();
  }
  
  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskExecutor());
    taskRegistrar.addTriggerTask(
        login::clearExpiredTokens,
        context -> Stream.<Instant>builder()
            // get the next token expiration time
            .add(login.getOldestToken()
                .map(time -> time.plus(properties.getTokenLifetime()))
                .orElse(null))
            // get the last execution time
            .add(Optional.ofNullable(context.lastActualExecutionTime())
                .map(Date::toInstant)
                .orElse(null))
            // get the time right now
            .add(Instant.now().plus(properties.getTokenLifetime()))
            .build()
            // filter out the ones that might be missing
            .filter(Objects::nonNull)
            .max(Instant::compareTo)
            .map(time -> time.plusSeconds(5))
            .map(Date::from)
            .orElseThrow(() -> new IllegalStateException("this should not be happening"))
    );
  }
}
