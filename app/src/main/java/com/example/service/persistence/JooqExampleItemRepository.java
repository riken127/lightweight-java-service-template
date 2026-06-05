package com.example.service.persistence;

import com.example.service.application.ExampleItemRepository;
import com.example.service.domain.ExampleItem;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

/** Repository backed by jOOQ for the tiny technical example table. */
public final class JooqExampleItemRepository implements ExampleItemRepository {
  private static final Table<?> EXAMPLE_ITEMS = DSL.table(DSL.name("example_items"));
  private static final Field<UUID> ID = DSL.field(DSL.name("id"), UUID.class);
  private static final Field<String> NAME = DSL.field(DSL.name("name"), String.class);
  private static final Field<OffsetDateTime> CREATED_AT =
      DSL.field(DSL.name("created_at"), OffsetDateTime.class);

  private final DSLContext dsl;

  /** Creates a repository using the provided jOOQ context. */
  public JooqExampleItemRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public List<ExampleItem> findRecent(int limit) {
    return dsl.select(ID, NAME, CREATED_AT)
        .from(EXAMPLE_ITEMS)
        .orderBy(CREATED_AT.desc())
        .limit(limit)
        .fetch(
            record ->
                new ExampleItem(
                    record.get(ID), record.get(NAME), record.get(CREATED_AT).toInstant()));
  }

  /** Stores an item for tests and small local experiments. */
  public void save(ExampleItem item) {
    dsl.insertInto(EXAMPLE_ITEMS)
        .columns(ID, NAME, CREATED_AT)
        .values(item.id(), item.name(), OffsetDateTime.ofInstant(item.createdAt(), ZoneOffset.UTC))
        .onConflict(ID)
        .doNothing()
        .execute();
  }
}
