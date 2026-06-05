package com.example.service.http;

import com.example.service.application.ExampleService;
import com.example.service.observability.ReadinessCheck;
import com.example.service.observability.RequestCorrelation;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import java.util.List;

/** Creates the configured Javalin HTTP server. */
public final class HttpServerFactory {
  private HttpServerFactory() {}

  /** Creates a Javalin instance with JSON mapping, middleware, routes, and errors. */
  public static Javalin create(
      String serviceName, ExampleService exampleService, List<ReadinessCheck> readinessChecks) {
    HealthRoutes healthRoutes = new HealthRoutes(serviceName, readinessChecks);
    ExampleRoutes exampleRoutes = new ExampleRoutes(exampleService);
    ErrorMapper errorMapper = new ErrorMapper();

    return Javalin.create(
        config -> {
          config.jsonMapper(
              new JavalinJackson()
                  .updateMapper(
                      mapper -> {
                        mapper.registerModule(new JavaTimeModule());
                        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                      }));
          config.router.ignoreTrailingSlashes = true;

          config.routes.before(RequestCorrelation::before);
          config.routes.after(RequestCorrelation::after);
          config.routes.get("/health", healthRoutes::health);
          config.routes.get("/ready", healthRoutes::ready);
          config.routes.get("/v1/example", exampleRoutes::list);
          config.routes.exception(IllegalArgumentException.class, errorMapper::badRequest);
          config.routes.exception(Exception.class, errorMapper::internalError);
          config.routes.error(404, errorMapper::notFound);
        });
  }
}
