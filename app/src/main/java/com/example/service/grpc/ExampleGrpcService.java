package com.example.service.grpc;

import com.example.service.application.ExampleService;
import com.example.service.domain.ExampleItem;
import com.example.service.grpc.proto.ExampleItemsApiGrpc;
import com.example.service.grpc.proto.ListExampleItemsRequest;
import com.example.service.grpc.proto.ListExampleItemsResponse;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Maps protobuf requests onto application services. */
public final class ExampleGrpcService extends ExampleItemsApiGrpc.ExampleItemsApiImplBase {
  private static final Logger LOG = LoggerFactory.getLogger(ExampleGrpcService.class);

  private final ExampleService exampleService;

  /** Creates the adapter with its application service dependency. */
  public ExampleGrpcService(ExampleService exampleService) {
    this.exampleService = exampleService;
  }

  @Override
  public void listExampleItems(
      ListExampleItemsRequest request, StreamObserver<ListExampleItemsResponse> responseObserver) {
    try {
      Integer limit = request.hasLimit() ? request.getLimit() : null;
      ListExampleItemsResponse response =
          ListExampleItemsResponse.newBuilder()
              .addAllItems(exampleService.recentItems(limit).stream().map(this::toGrpc).toList())
              .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (IllegalArgumentException exception) {
      responseObserver.onError(
          Status.INVALID_ARGUMENT.withDescription(exception.getMessage()).asRuntimeException());
    } catch (RuntimeException exception) {
      LOG.warn(
          "gRPC example items request failed requestId={}",
          GrpcRequestContext.REQUEST_ID.get(),
          exception);
      responseObserver.onError(
          Status.INTERNAL.withDescription("internal error").asRuntimeException());
    }
  }

  private com.example.service.grpc.proto.ExampleItem toGrpc(ExampleItem item) {
    return com.example.service.grpc.proto.ExampleItem.newBuilder()
        .setId(item.id().toString())
        .setName(item.name())
        .setCreatedAt(toTimestamp(item.createdAt()))
        .build();
  }

  private static Timestamp toTimestamp(Instant instant) {
    return Timestamp.newBuilder()
        .setSeconds(instant.getEpochSecond())
        .setNanos(instant.getNano())
        .build();
  }
}
