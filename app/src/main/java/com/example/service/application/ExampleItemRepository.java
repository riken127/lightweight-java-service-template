package com.example.service.application;

import com.example.service.domain.ExampleItem;
import java.util.List;

/** Application-facing port for reading example items. */
public interface ExampleItemRepository {
  /** Finds the most recent items up to the requested limit. */
  List<ExampleItem> findRecent(int limit);
}
