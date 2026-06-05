package com.example.service.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GrpcHealthServiceTest {
  private Server server;
  private ManagedChannel channel;

  @BeforeEach
  void startServer() throws Exception {
    String serverName = InProcessServerBuilder.generateName();
    server =
        InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(new GrpcHealthService("service-template"))
            .build()
            .start();
    channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
  }

  @AfterEach
  void stopServer() {
    if (channel != null) {
      channel.shutdownNow();
    }
    if (server != null) {
      server.shutdownNow();
    }
  }

  @Test
  void reportsServingForAllServices() {
    HealthCheckResponse response =
        HealthGrpc.newBlockingStub(channel).check(HealthCheckRequest.newBuilder().build());

    assertThat(response.getStatus()).isEqualTo(HealthCheckResponse.ServingStatus.SERVING);
  }

  @Test
  void rejectsUnknownServices() {
    assertThatThrownBy(
            () ->
                HealthGrpc.newBlockingStub(channel)
                    .check(HealthCheckRequest.newBuilder().setService("missing").build()))
        .isInstanceOfSatisfying(
            StatusRuntimeException.class,
            exception ->
                assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND));
  }
}
