package com.example.service.config;

/** HTTP server configuration. */
public record ServerConfig(int port) {
  /** Creates validated server configuration. */
  public ServerConfig {
    if (port < 0 || port > 65_535) {
      throw new IllegalArgumentException("HTTP port must be between 0 and 65535");
    }
  }
}
