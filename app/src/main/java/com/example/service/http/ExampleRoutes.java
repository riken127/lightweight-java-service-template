package com.example.service.http;

import com.example.service.application.ExampleService;
import com.example.service.domain.ExampleItem;
import com.example.service.http.dto.ExampleItemResponse;
import com.example.service.http.dto.ExampleItemsResponse;
import io.javalin.http.Context;
import java.util.List;

final class ExampleRoutes {
  private final ExampleService exampleService;

  ExampleRoutes(ExampleService exampleService) {
    this.exampleService = exampleService;
  }

  void list(Context ctx) {
    Integer limit = parseLimit(ctx.queryParam("limit"));
    List<ExampleItemResponse> items =
        exampleService.recentItems(limit).stream().map(this::toResponse).toList();
    ctx.json(new ExampleItemsResponse(items));
  }

  private Integer parseLimit(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return Integer.parseInt(value);
  }

  private ExampleItemResponse toResponse(ExampleItem item) {
    return new ExampleItemResponse(item.id(), item.name(), item.createdAt());
  }
}
