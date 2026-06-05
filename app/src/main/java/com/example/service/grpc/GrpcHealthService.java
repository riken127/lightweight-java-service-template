package com.example.service.grpc;

import io.grpc.Status;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;
import java.util.Set;

final class GrpcHealthService extends HealthGrpc.HealthImplBase {
  private final Set<String> servingServices;

  GrpcHealthService(String serviceName) {
    this.servingServices = Set.of("", serviceName);
  }

  @Override
  public void check(
      HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
    if (!servingServices.contains(request.getService())) {
      responseObserver.onError(
          Status.NOT_FOUND.withDescription("unknown service").asRuntimeException());
      return;
    }

    responseObserver.onNext(
        HealthCheckResponse.newBuilder()
            .setStatus(HealthCheckResponse.ServingStatus.SERVING)
            .build());
    responseObserver.onCompleted();
  }

  @Override
  public void watch(
      HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
    responseObserver.onError(
        Status.UNIMPLEMENTED.withDescription("watch is not implemented").asRuntimeException());
  }
}
