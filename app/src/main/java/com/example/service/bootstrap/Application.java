package com.example.service.bootstrap;

import com.zaxxer.hikari.HikariDataSource;
import io.grpc.Server;
import io.javalin.Javalin;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Owns running servers and resources that must close during shutdown. */
public final class Application implements AutoCloseable {
  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  private final Javalin server;
  private final Server grpcServer;
  private final HikariDataSource dataSource;
  private final int port;
  private volatile boolean started;

  Application(Javalin server, Server grpcServer, HikariDataSource dataSource, int port) {
    this.server = server;
    this.grpcServer = grpcServer;
    this.dataSource = dataSource;
    this.port = port;
  }

  /** Starts HTTP and gRPC servers, then registers graceful shutdown handling. */
  public void start() {
    try {
      grpcServer.start();
      LOG.info("gRPC server started on port {}", grpcServer.getPort());
      server.start(port);
      started = true;
      LOG.info("HTTP server started on port {}", actualPort());
      Runtime.getRuntime().addShutdownHook(new Thread(this::close, "service-shutdown"));
    } catch (IOException | RuntimeException exception) {
      close();
      throw new IllegalStateException("Failed to start application", exception);
    }
  }

  /** Returns the bound HTTP port, including the random port chosen for tests. */
  public int actualPort() {
    return server.port();
  }

  @Override
  public void close() {
    if (started) {
      LOG.info("Stopping HTTP server");
      server.stop();
      started = false;
    }
    if (!grpcServer.isShutdown()) {
      LOG.info("Stopping gRPC server");
      grpcServer.shutdown();
      try {
        if (!grpcServer.awaitTermination(10, TimeUnit.SECONDS)) {
          grpcServer.shutdownNow();
        }
      } catch (InterruptedException exception) {
        Thread.currentThread().interrupt();
        grpcServer.shutdownNow();
      }
    }
    LOG.info("Closing database pool");
    dataSource.close();
  }
}
