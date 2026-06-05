package com.example.service.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.service.domain.ExampleItem;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class JooqExampleItemRepositoryIntegrationTest {
  @Container
  private static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:18-alpine");

  private HikariDataSource dataSource;
  private JooqExampleItemRepository repository;

  @BeforeEach
  void setUp() {
    HikariConfig hikari = new HikariConfig();
    hikari.setJdbcUrl(POSTGRES.getJdbcUrl());
    hikari.setUsername(POSTGRES.getUsername());
    hikari.setPassword(POSTGRES.getPassword());
    hikari.setMaximumPoolSize(2);
    dataSource = new HikariDataSource(hikari);

    Flyway.configure().dataSource(dataSource).locations("classpath:db/migration").load().migrate();

    DSLContext dsl = DSL.using(dataSource, SQLDialect.POSTGRES);
    repository = new JooqExampleItemRepository(dsl);
  }

  @AfterEach
  void tearDown() {
    dataSource.close();
  }

  @Test
  void storesAndReadsExampleItems() {
    ExampleItem item =
        new ExampleItem(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "template",
            Instant.parse("2026-01-01T00:00:00Z"));

    repository.save(item);

    List<ExampleItem> items = repository.findRecent(10);
    assertThat(items).containsExactly(item);
  }
}
