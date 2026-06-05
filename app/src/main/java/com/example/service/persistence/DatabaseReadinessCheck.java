package com.example.service.persistence;

import com.example.service.observability.ReadinessCheck;
import com.example.service.observability.ReadinessResult;
import org.jooq.DSLContext;

/** Readiness check that verifies PostgreSQL accepts a simple query. */
public final class DatabaseReadinessCheck implements ReadinessCheck {
  private final DSLContext dsl;

  /** Creates a database readiness check backed by jOOQ. */
  public DatabaseReadinessCheck(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public ReadinessResult check() {
    try {
      dsl.fetchValue("select 1");
      return ReadinessResult.ready("database");
    } catch (RuntimeException e) {
      return ReadinessResult.notReady("database", "Database check failed");
    }
  }
}
