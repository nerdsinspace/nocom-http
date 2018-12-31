package com.matt.nocom.server.config;

import com.matt.nocom.server.util.JOOQToSpringExceptionTransformer;
import java.nio.file.Paths;
import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.sqlite.SQLiteDataSource;

@Configuration
@ComponentScan({"com.matt.nocom.server.service"})
@EnableTransactionManagement
@PropertySource("database.properties")
public class DatabaseConfiguration {
  private final Environment env;

  @Autowired
  public DatabaseConfiguration(Environment env) {
    this.env = env;
  }

  @Bean
  public DataSource dataSource() {
    SQLiteDataSource src = new SQLiteDataSource();
    src.setUrl("jdbc:sqlite:" + Paths.get("")
        .resolve(env.getRequiredProperty("db.file"))
        .toAbsolutePath()
        .toString());
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
    config.set(SQLDialect.SQLITE);
    return config;
  }

  @Bean
  public DSLContext dsl() {
    return new DefaultDSLContext(configuration());
  }

  @Bean
  public DataSourceInitializer dataSourceInitializer() {
    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource());

    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.addScript(new ClassPathResource(env.getRequiredProperty("db.schema")));

    initializer.setDatabasePopulator(populator);
    return initializer;
  }
}
