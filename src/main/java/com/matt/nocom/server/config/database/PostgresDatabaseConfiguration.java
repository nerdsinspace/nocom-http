package com.matt.nocom.server.config.database;

import com.google.common.base.Strings;
import com.matt.nocom.server.properties.PostgresDatabaseProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.*;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetSocketAddress;

@Configuration
@EnableConfigurationProperties(PostgresDatabaseProperties.class)
@EnableTransactionManagement
@RequiredArgsConstructor
public class PostgresDatabaseConfiguration {
  private final PostgresDatabaseProperties properties;

  @Bean(destroyMethod = "close")
  @DependsOn("databasePortForwarder")
  @Lazy
  public BasicDataSource dataSourcePostgres() {
    BasicDataSource src = new BasicDataSource();
    src.setDriverClassName("org.postgresql.Driver");

    if(properties.getUsername() != null) {
      src.setUsername(properties.getUsername());
      src.setPassword(Strings.nullToEmpty(properties.getPassword()));
    }

    src.setUrl("jdbc:postgresql://"
        + InetSocketAddress.createUnresolved(properties.getHostname(), properties.getPort()).toString()
        + "/"
        + properties.getDatabase());
    src.setConnectionProperties("readOnly=true");

    return src;
  }

  @Bean
  @Lazy
  public LazyConnectionDataSourceProxy lazyConnectionDataSourcePostgres() {
    return new LazyConnectionDataSourceProxy(dataSourcePostgres());
  }

  @Bean
  @Lazy
  public TransactionAwareDataSourceProxy transactionAwareDataSourcePostgres() {
    return new TransactionAwareDataSourceProxy(lazyConnectionDataSourcePostgres());
  }

  @Bean
  @Lazy
  public DataSourceTransactionManager transactionManagerPostgres() {
    return new DataSourceTransactionManager(transactionAwareDataSourcePostgres());
  }

  @Bean
  @Lazy
  public DataSourceConnectionProvider connectionProviderPostgres() {
    return new DataSourceConnectionProvider(transactionAwareDataSourcePostgres());
  }

  @Bean
  @Lazy
  public DefaultConfiguration configurationPostgres() {
    DefaultConfiguration config = new DefaultConfiguration();
    config.set(connectionProviderPostgres()); // and again
    config.set(new DefaultExecuteListenerProvider(new JooqExceptionTranslator()));
    config.set(SQLDialect.POSTGRES);
    return config;
  }

  @Bean(destroyMethod = "close")
  @Lazy
  public DSLContext postgresDsl() {
    return new DefaultDSLContext(configurationPostgres());
  }
}
