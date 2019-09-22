package com.matt.nocom.server.config;

import com.matt.nocom.server.service.ApplicationSettings;
import com.matt.nocom.server.service.auth.LoginService;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
  
  private final ApplicationSettings settings;
  private final LoginService login;
  
  public TokenExpirationSchedulerConfiguration(
      ApplicationSettings settings, LoginService login) {
    this.settings = settings;
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
        context -> login.getNextTokenExpirationTime()
            .map(time -> Math.max(time, System.currentTimeMillis()))
            .map(Instant::ofEpochMilli)
            .map(in -> in.plusSeconds(5))
            .map(Date::from)
            .orElseGet(() -> Optional.ofNullable(context.lastActualExecutionTime())
                .map(Date::toInstant)
                .map(time -> time.plusMillis(settings.getTokenExpiration())) // a new token will never be under this time
                .map(Date::from)
                    .orElseGet(() -> Date.from(Instant.now().plusSeconds(5)))
                // run expire tokens once on startup
            )
    );
  }
}
