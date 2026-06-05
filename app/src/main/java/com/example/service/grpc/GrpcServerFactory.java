package com.example.service.grpc;

import com.example.service.application.ExampleService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionServiceV1;

/** Creates the gRPC server and registers explicitly wired service adapters. */
public final class GrpcServerFactory {
  private GrpcServerFactory() {}

  /** Creates a gRPC server without starting it. */
  public static Server create(int port, String serviceName, ExampleService exampleService) {
    return ServerBuilder.forPort(port)
        .intercept(new GrpcRequestCorrelationInterceptor())
        .addService(new ExampleGrpcService(exampleService))
        .addService(new GrpcHealthService(serviceName))
        .addService(ProtoReflectionServiceV1.newInstance())
        .build();
  }
}
