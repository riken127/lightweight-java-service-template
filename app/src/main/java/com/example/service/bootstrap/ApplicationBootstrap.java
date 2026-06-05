package com.example.service.bootstrap;

import com.example.service.application.ExampleService;
import com.example.service.config.AppConfig;
import com.example.service.config.DatabaseConfig;
import com.example.service.http.HttpServerFactory;
import com.example.service.observability.ReadinessCheck;
import com.example.service.persistence.DatabaseReadinessCheck;
import com.example.service.persistence.JooqExampleItemRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import java.util.List;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Builds the application object graph using explicit constructor wiring. */
public final class ApplicationBootstrap {
  private static final Logger LOG = LoggerFactory.getLogger(ApplicationBootstrap.class);

  private ApplicationBootstrap() {}

  /** Creates a configured application without starting the HTTP server. */
  public static Application create(AppConfig config) {
    LOG.info("Starting {} with Java {}", config.serviceName(), Runtime.version());

    HikariDataSource dataSource = createDataSource(config.database());
    migrate(dataSource);

    DSLContext dsl = DSL.using(dataSource, SQLDialect.POSTGRES);
    JooqExampleItemRepository repository = new JooqExampleItemRepository(dsl);
    ExampleService exampleService = new ExampleService(repository);
    List<ReadinessCheck> readinessChecks = List.of(new DatabaseReadinessCheck(dsl));

    Javalin server =
        HttpServerFactory.create(config.serviceName(), exampleService, readinessChecks);
    return new Application(server, dataSource, config.server().port());
  }

  static HikariDataSource createDataSource(DatabaseConfig database) {
    HikariConfig hikari = new HikariConfig();
    hikari.setJdbcUrl(database.url());
    hikari.setUsername(database.username());
    hikari.setPassword(database.password());
    hikari.setMaximumPoolSize(database.maxPoolSize());
    hikari.setPoolName("service-template-db");
    return new HikariDataSource(hikari);
  }

  static void migrate(HikariDataSource dataSource) {
    Flyway.configure().dataSource(dataSource).locations("classpath:db/migration").load().migrate();
  }
}
