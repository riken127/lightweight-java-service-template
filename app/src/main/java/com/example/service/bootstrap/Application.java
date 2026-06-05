package com.example.service.bootstrap;

import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Owns the running HTTP server and resources that must close during shutdown. */
public final class Application implements AutoCloseable {
  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  private final Javalin server;
  private final HikariDataSource dataSource;
  private final int port;
  private volatile boolean started;

  Application(Javalin server, HikariDataSource dataSource, int port) {
    this.server = server;
    this.dataSource = dataSource;
    this.port = port;
  }

  /** Starts the HTTP server and registers graceful shutdown handling. */
  public void start() {
    server.start(port);
    started = true;
    LOG.info("HTTP server started on port {}", actualPort());
    Runtime.getRuntime().addShutdownHook(new Thread(this::close, "service-shutdown"));
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
    LOG.info("Closing database pool");
    dataSource.close();
  }
}
