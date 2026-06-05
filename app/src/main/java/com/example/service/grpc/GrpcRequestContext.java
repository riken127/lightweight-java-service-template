package com.example.service.grpc;

import io.grpc.Context;

/** Request-scoped values shared by gRPC interceptors and service implementations. */
final class GrpcRequestContext {
  static final Context.Key<String> REQUEST_ID = Context.key("request-id");

  private GrpcRequestContext() {}
}
