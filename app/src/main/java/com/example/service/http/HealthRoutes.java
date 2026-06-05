package com.example.service.http;

import com.example.service.http.dto.HealthResponse;
import com.example.service.observability.ReadinessCheck;
import com.example.service.observability.ReadinessResult;
import com.example.service.observability.RequestCorrelation;
import io.javalin.http.Context;
import java.time.Instant;
import java.util.List;

final class HealthRoutes {
  private final String serviceName;
  private final List<ReadinessCheck> readinessChecks;

  HealthRoutes(String serviceName, List<ReadinessCheck> readinessChecks) {
    this.serviceName = serviceName;
    this.readinessChecks = List.copyOf(readinessChecks);
  }

  void health(Context ctx) {
    ctx.json(
        new HealthResponse(
            serviceName, "UP", RequestCorrelation.requestId(ctx), Instant.now(), List.of()));
  }

  void ready(Context ctx) {
    List<ReadinessResult> checks = readinessChecks.stream().map(ReadinessCheck::check).toList();
    boolean ready = checks.stream().allMatch(ReadinessResult::ready);

    ctx.status(ready ? 200 : 503);
    ctx.json(
        new HealthResponse(
            serviceName,
            ready ? "READY" : "NOT_READY",
            RequestCorrelation.requestId(ctx),
            Instant.now(),
            checks));
  }
}
