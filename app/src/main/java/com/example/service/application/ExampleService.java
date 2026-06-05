package com.example.service.application;

import com.example.service.domain.ExampleItem;
import java.util.List;

/** Minimal application service that demonstrates orchestration outside HTTP handlers. */
public final class ExampleService {
  private static final int DEFAULT_LIMIT = 20;
  private static final int MAX_LIMIT = 100;

  private final ExampleItemRepository repository;

  /** Creates the service with its repository dependency. */
  public ExampleService(ExampleItemRepository repository) {
    this.repository = repository;
  }

  /** Returns recent example items after applying service-level limit rules. */
  public List<ExampleItem> recentItems(Integer requestedLimit) {
    int limit = normalizeLimit(requestedLimit);
    return repository.findRecent(limit);
  }

  private static int normalizeLimit(Integer requestedLimit) {
    if (requestedLimit == null) {
      return DEFAULT_LIMIT;
    }
    if (requestedLimit < 1) {
      throw new IllegalArgumentException("limit must be greater than zero");
    }
    return Math.min(requestedLimit, MAX_LIMIT);
  }
}
