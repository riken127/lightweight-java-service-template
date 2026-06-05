package com.example.service.domain;

import java.time.Instant;
import java.util.UUID;

/** Framework-free domain record for the tiny technical example. */
public record ExampleItem(UUID id, String name, Instant createdAt) {
  /** Creates a validated example item. */
  public ExampleItem {
    if (id == null) {
      throw new IllegalArgumentException("Example item id must not be null");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Example item name must not be blank");
    }
    if (createdAt == null) {
      throw new IllegalArgumentException("Example item createdAt must not be null");
    }
  }
}
