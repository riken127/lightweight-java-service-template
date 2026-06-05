package com.example.service.http;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.service.application.ExampleService;
import com.example.service.domain.ExampleItem;
import com.example.service.observability.ReadinessResult;
import com.example.service.support.HttpResponse;
import com.example.service.support.TestHttpClient;
import io.javalin.Javalin;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class HttpRoutesTest {
  private Javalin app;

  @AfterEach
  void stopServer() {
    if (app != null) {
      app.stop();
    }
  }

  @Test
  void healthReturnsOkWithRequestId() throws Exception {
    startServer();

    HttpResponse response = TestHttpClient.get(app, "/health", "test-request-id");

    assertThat(response.status()).isEqualTo(200);
    assertThat(response.header("X-Request-Id")).isEqualTo("test-request-id");
    assertThat(response.body()).contains("\"status\":\"UP\"");
    assertThat(response.body()).contains("\"requestId\":\"test-request-id\"");
  }

  @Test
  void readyReturnsUnavailableWhenDependencyFails() throws Exception {
    app =
        HttpServerFactory.create(
            "service-template",
            new ExampleService(limit -> List.of()),
            List.of(() -> ReadinessResult.notReady("database", "Database check failed")));
    app.start(0);

    HttpResponse response = TestHttpClient.get(app, "/ready");

    assertThat(response.status()).isEqualTo(503);
    assertThat(response.body()).contains("\"status\":\"NOT_READY\"");
  }

  @Test
  void exampleRouteReturnsItems() throws Exception {
    startServer();

    HttpResponse response = TestHttpClient.get(app, "/v1/example?limit=2");

    assertThat(response.status()).isEqualTo(200);
    assertThat(response.body()).contains("\"items\"");
    assertThat(response.body()).contains("\"name\":\"template\"");
  }

  @Test
  void invalidLimitReturnsJsonError() throws Exception {
    startServer();

    HttpResponse response = TestHttpClient.get(app, "/v1/example?limit=0");

    assertThat(response.status()).isEqualTo(400);
    assertThat(response.body()).contains("\"code\":\"bad_request\"");
    assertThat(response.body()).doesNotContain("Exception");
  }

  private void startServer() {
    app =
        HttpServerFactory.create(
            "service-template",
            new ExampleService(
                limit ->
                    List.of(
                        new ExampleItem(
                            UUID.fromString("00000000-0000-0000-0000-000000000001"),
                            "template",
                            Instant.EPOCH))),
            List.of(() -> ReadinessResult.ready("database")));
    app.start(0);
  }
}
