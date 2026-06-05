package com.example.service.grpc;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import java.util.UUID;

/** Adds a lightweight request id to each gRPC call and returns it in response metadata. */
public final class GrpcRequestCorrelationInterceptor implements ServerInterceptor {
  static final Metadata.Key<String> REQUEST_ID_HEADER =
      Metadata.Key.of("x-request-id", Metadata.ASCII_STRING_MARSHALLER);

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
    String requestId = requestId(headers);
    ServerCall<ReqT, RespT> correlatedCall =
        new SimpleForwardingServerCall<>(call) {
          @Override
          public void sendHeaders(Metadata responseHeaders) {
            responseHeaders.put(REQUEST_ID_HEADER, requestId);
            super.sendHeaders(responseHeaders);
          }
        };
    Context context = Context.current().withValue(GrpcRequestContext.REQUEST_ID, requestId);
    return Contexts.interceptCall(context, correlatedCall, headers, next);
  }

  private static String requestId(Metadata headers) {
    String requestId = headers.get(REQUEST_ID_HEADER);
    return requestId == null || requestId.isBlank() ? UUID.randomUUID().toString() : requestId;
  }
}
