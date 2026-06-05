package com.example.service.support;

import io.javalin.Javalin;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

/** Tiny JDK HTTP client wrapper for tests that call a running Javalin server. */
public final class TestHttpClient {
  private static final HttpClient CLIENT = HttpClient.newHttpClient();

  private TestHttpClient() {}

  /** Sends a GET request without a caller-provided request id. */
  public static HttpResponse get(Javalin app, String path)
      throws IOException, InterruptedException {
    return get(app, path, null);
  }

  /** Sends a GET request with an optional X-Request-Id header. */
  public static HttpResponse get(Javalin app, String path, String requestId)
      throws IOException, InterruptedException {
    HttpRequest.Builder builder =
        HttpRequest.newBuilder().uri(URI.create("http://localhost:" + app.port() + path)).GET();
    if (requestId != null) {
      builder.header("X-Request-Id", requestId);
    }

    java.net.http.HttpResponse<String> response =
        CLIENT.send(builder.build(), BodyHandlers.ofString());
    return new HttpResponse(response.statusCode(), response.headers(), response.body());
  }
}
