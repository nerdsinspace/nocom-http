package com.matt.nocom.server.config;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.service.ApplicationSettings;
import com.matt.nocom.server.service.DatabaseInitializer;
import com.matt.nocom.server.util.JOOQToSpringExceptionTransformer;
import java.nio.file.Path;
import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.sqlite.SQLiteDataSource;

@Configuration
@ComponentScan({"com.matt.nocom.server.service"})
@EnableTransactionManagement
public class DatabaseConfiguration implements Logging {
  private final Path database;
  private final ApplicationSettings settings;
  private final DatabaseInitializer initializer;

  @Autowired
  public DatabaseConfiguration(Path database,
      ApplicationSettings settings,
      DatabaseInitializer initializer) {
    this.database = database;
    this.settings = settings;
    this.initializer = initializer;
  }

  @Bean
  public DataSource dataSource() {
    SQLiteDataSource src = new SQLiteDataSource();
    src.setUrl("jdbc:sqlite:" + database.toAbsolutePath().toString());
    return src;
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
  public DataSourceTransactionManager transactionManager() {
    return new DataSourceTransactionManager(transactionAwareDataSource());
  }

  @Bean
  public DataSourceConnectionProvider connectionProvider() {
    return new DataSourceConnectionProvider(transactionAwareDataSource());
  }

  @Bean
  public JOOQToSpringExceptionTransformer jooqToSpringExceptionTransformer() {
    return new JOOQToSpringExceptionTransformer();
  }

  @Bean
  public DefaultConfiguration configuration() {
    DefaultConfiguration config = new DefaultConfiguration();
    config.set(connectionProvider());
    config.set(new DefaultExecuteListenerProvider(jooqToSpringExceptionTransformer()));
    config.set(settings.getDialect());
    return config;
  }

  @Bean
  public DSLContext dsl() {
    return new DefaultDSLContext(configuration());
  }

  @Bean
  @DependsOn({"flyway", "flywayInitializer"})
  public DataSourceInitializer dataSourceInitializer() {
    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource());
    initializer.setDatabasePopulator(this.initializer);
    return initializer;
  }
}
