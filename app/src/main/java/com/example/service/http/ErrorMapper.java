package com.example.service.http;

import com.example.service.http.dto.ErrorResponse;
import com.example.service.observability.RequestCorrelation;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ErrorMapper {
  private static final Logger LOG = LoggerFactory.getLogger(ErrorMapper.class);

  void badRequest(IllegalArgumentException exception, Context ctx) {
    ctx.status(400);
    ctx.json(error(ctx, "bad_request", exception.getMessage()));
  }

  void internalError(Exception exception, Context ctx) {
    LOG.error("Unhandled request failure", exception);
    ctx.status(500);
    ctx.json(error(ctx, "internal_error", "Unexpected server error"));
  }

  void notFound(Context ctx) {
    ctx.json(error(ctx, "not_found", "Route not found"));
  }

  private ErrorResponse error(Context ctx, String code, String message) {
    return new ErrorResponse(code, message, RequestCorrelation.requestId(ctx));
  }
}
