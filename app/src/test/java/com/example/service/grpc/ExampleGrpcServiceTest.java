package com.example.service.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.service.application.ExampleService;
import com.example.service.domain.ExampleItem;
import com.example.service.grpc.proto.ExampleItemsApiGrpc;
import com.example.service.grpc.proto.ListExampleItemsRequest;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.MetadataUtils;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExampleGrpcServiceTest {
  private static final Metadata.Key<String> REQUEST_ID_HEADER =
      Metadata.Key.of("x-request-id", Metadata.ASCII_STRING_MARSHALLER);

  private Server server;
  private ManagedChannel channel;
  private AtomicReference<Metadata> responseHeaders;
  private AtomicReference<Metadata> responseTrailers;

  @BeforeEach
  void startServer() throws Exception {
    String serverName = InProcessServerBuilder.generateName();
    ExampleService exampleService =
        new ExampleService(
            limit ->
                List.of(
                    new ExampleItem(
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        "template",
                        Instant.EPOCH)));
    server =
        InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .intercept(new GrpcRequestCorrelationInterceptor())
            .addService(new ExampleGrpcService(exampleService))
            .build()
            .start();
    channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
    responseHeaders = new AtomicReference<>();
    responseTrailers = new AtomicReference<>();
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
  void listExampleItemsReturnsDomainItems() {
    Metadata requestHeaders = new Metadata();
    requestHeaders.put(REQUEST_ID_HEADER, "grpc-test-request");
    ExampleItemsApiGrpc.ExampleItemsApiBlockingStub stub =
        blockingStub()
            .withInterceptors(
                MetadataUtils.newAttachHeadersInterceptor(requestHeaders),
                MetadataUtils.newCaptureMetadataInterceptor(responseHeaders, responseTrailers));

    var response = stub.listExampleItems(ListExampleItemsRequest.newBuilder().setLimit(1).build());

    assertThat(response.getItemsList()).hasSize(1);
    assertThat(response.getItems(0).getName()).isEqualTo("template");
    assertThat(response.getItems(0).getCreatedAt().getSeconds()).isEqualTo(0);
    assertThat(responseHeaders.get().get(REQUEST_ID_HEADER)).isEqualTo("grpc-test-request");
  }

  @Test
  void invalidLimitReturnsInvalidArgument() {
    ExampleItemsApiGrpc.ExampleItemsApiBlockingStub stub = blockingStub();

    assertThatThrownBy(
            () -> stub.listExampleItems(ListExampleItemsRequest.newBuilder().setLimit(0).build()))
        .isInstanceOfSatisfying(
            StatusRuntimeException.class,
            exception ->
                assertThat(exception.getStatus().getCode())
                    .isEqualTo(Status.Code.INVALID_ARGUMENT));
  }

  private ExampleItemsApiGrpc.ExampleItemsApiBlockingStub blockingStub() {
    return ExampleItemsApiGrpc.newBlockingStub(channel);
  }
}
