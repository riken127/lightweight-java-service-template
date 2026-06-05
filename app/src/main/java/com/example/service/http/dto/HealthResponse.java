package com.example.service.http.dto;

import com.example.service.observability.ReadinessResult;
import java.time.Instant;
import java.util.List;

/** JSON response used by liveness and readiness endpoints. */
public record HealthResponse(
    String serviceName,
    String status,
    String requestId,
    Instant checkedAt,
    List<ReadinessResult> checks) {}
