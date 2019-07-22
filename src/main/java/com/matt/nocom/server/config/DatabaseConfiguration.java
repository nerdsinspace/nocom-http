package com.matt.nocom.server.config;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.Properties;
import com.matt.nocom.server.service.DatabaseInitializer;
import com.matt.nocom.server.util.JOOQToSpringExceptionTransformer;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.sqlite.SQLiteDataSource;

@Configuration
@ComponentScan({"com.matt.nocom.server.service"})
@EnableTransactionManagement
@PropertySource("database.properties")
public class DatabaseConfiguration implements Logging {
  private final Environment env;
  private final DatabaseInitializer databaseInitializer;

  @Autowired
  public DatabaseConfiguration(Environment env,
      DatabaseInitializer databaseInitializer) {
    this.env = env;
    this.databaseInitializer = databaseInitializer;
  }

  private Path getDatabasePath() {
    return Paths.get("")
        .resolve(env.getRequiredProperty("db.file"));
  }

  @Bean
  public DataSource dataSource() {
    SQLiteDataSource src = new SQLiteDataSource();
    src.setUrl("jdbc:sqlite:" + getDatabasePath().toAbsolutePath().toString());
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
    config.set(Properties.SQL_DIALECT);
    return config;
  }

  @Bean
  public DSLContext dsl() {
    return new DefaultDSLContext(configuration());
  }

  @Bean
  public DataSourceInitializer dataSourceInitializer() {
    DataSourceInitializer initializer = new DataSourceInitializer();

    DataSource dataSource = dataSource();

    // start with flyway migration
    Flyway flyway = Flyway.configure()
        .dataSource(dataSource)
        .baselineOnMigrate(true)
        .baselineVersion("2") // version 1 will overwrite the existing tables (not good)
        .load();
    flyway.migrate();

    initializer.setDataSource(dataSource());
    initializer.setDatabasePopulator(databaseInitializer);
    return initializer;
  }
}
