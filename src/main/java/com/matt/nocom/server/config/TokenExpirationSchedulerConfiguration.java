package com.matt.nocom.server.config;

import com.matt.nocom.server.Properties;
import com.matt.nocom.server.service.LoginManagerService;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
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
  private final LoginManagerService login;

  public TokenExpirationSchedulerConfiguration(LoginManagerService login) {
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
        context -> login.getNextExpirationTime()
            .map(time -> Math.max(time, System.currentTimeMillis()))
            .map(Instant::ofEpochMilli)
            .map(in -> in.plusSeconds(30))
            .map(Date::from)
            .orElseGet(() -> Optional.ofNullable(context.lastActualExecutionTime())
                .map(Date::toInstant)
                .map(time -> time.plusMillis(Properties.TOKEN_EXPIRATION)) // a new token will never be under this time
                .map(Date::from)
                .orElseGet(() -> Date.from(Instant.now().plusSeconds(30))) // run expire tokens once on startup
            )
    );
  }
}
