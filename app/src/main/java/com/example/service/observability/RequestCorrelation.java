package com.example.service.observability;

import io.javalin.http.Context;
import java.util.UUID;
import org.slf4j.MDC;

/** Manages per-request correlation ids for responses and logging context. */
public final class RequestCorrelation {
  private static final String HEADER = "X-Request-Id";
  private static final String ATTRIBUTE = "requestId";

  private RequestCorrelation() {}

  /** Reads or creates a request id before route handling. */
  public static void before(Context ctx) {
    String requestId = ctx.header(HEADER);
    if (requestId == null || requestId.isBlank()) {
      requestId = UUID.randomUUID().toString();
    }
    ctx.attribute(ATTRIBUTE, requestId);
    ctx.header(HEADER, requestId);
    MDC.put(ATTRIBUTE, requestId);
  }

  /** Clears request-scoped logging state after route handling. */
  public static void after(Context ctx) {
    MDC.remove(ATTRIBUTE);
  }

  /** Returns the request id attached to the current Javalin context. */
  public static String requestId(Context ctx) {
    String requestId = ctx.attribute(ATTRIBUTE);
    return requestId == null ? "unknown" : requestId;
  }
}
