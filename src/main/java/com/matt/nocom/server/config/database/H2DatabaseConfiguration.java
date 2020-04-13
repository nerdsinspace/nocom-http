package com.matt.nocom.server.config.database;

import com.google.common.base.Strings;
import com.matt.nocom.server.properties.AuthenticationProperties;
import com.matt.nocom.server.properties.H2DatabaseProperties;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import static com.matt.nocom.server.Logging.getLogger;
import static com.matt.nocom.server.h2.codegen.Tables.AUTH_USERS;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(H2DatabaseProperties.class)
@Order(1)
@RequiredArgsConstructor
public class H2DatabaseConfiguration {
  private final H2DatabaseProperties properties;

  @Bean
  @Primary
  public DataSource dataSource() {
    var builder = DataSourceBuilder.create()
        .driverClassName("org.h2.Driver")
        .url("jdbc:h2:" + properties.getMode() + ":" + properties.getDatabase());

    if(properties.getUsername() != null) {
      builder.username(properties.getUsername());
      builder.password(Strings.nullToEmpty(properties.getPassword()));
    }

    return builder.build();
  }

  @Bean
  public LazyConnectionDataSourceProxy lazyConnectionDataSource() {
    return new LazyConnectionDataSourceProxy(dataSource());
  }

  @Bean
  public TransactionAwareDataSourceProxy transactionAwareDataSource() {
    return new TransactionAwareDataSourceProxy(lazyConnectionDataSource());
  }

  @Bean
  @Primary
  public DataSourceTransactionManager transactionManager() {
    return new DataSourceTransactionManager(transactionAwareDataSource());
  }

  @Bean
  @Primary
  public DataSourceConnectionProvider connectionProvider() {
    return new DataSourceConnectionProvider(transactionAwareDataSource());
  }

  @Bean
  @Primary
  public DefaultConfiguration configuration() {
    DefaultConfiguration config = new DefaultConfiguration();
    config.set(connectionProvider());
    config.set(new DefaultExecuteListenerProvider(new JooqExceptionTranslator()));
    config.set(SQLDialect.H2);
    return config;
  }

  @Bean(destroyMethod = "close")
  @DependsOn({"flyway", "flywayInitializer"})
  @Primary
  public DSLContext dsl() {
    return new DefaultDSLContext(configuration());
  }
}
