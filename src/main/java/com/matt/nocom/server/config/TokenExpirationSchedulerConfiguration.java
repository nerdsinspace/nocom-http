package com.matt.nocom.server.config;

import com.matt.nocom.server.service.LoginManagerService;
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
    return Executors.newScheduledThreadPool(1);
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskExecutor());
    taskRegistrar.addTriggerTask(
        login::clearExpiredTokens,
        context -> login.getNextExpirationTime()
            .map(Date::new)
            .orElseGet(() -> Optional.ofNullable(context.lastActualExecutionTime())
                .map(Date::toInstant)
                .map(time -> time.plusSeconds(3600))
                .map(Date::from)
                .orElseGet(Date::new)
            )
    );
  }
}
