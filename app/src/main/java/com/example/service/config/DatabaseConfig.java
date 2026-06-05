package com.example.service.config;

/** PostgreSQL connection pool configuration. */
public record DatabaseConfig(String url, String username, String password, int maxPoolSize) {
  /** Creates validated database configuration. */
  public DatabaseConfig {
    if (url == null || url.isBlank()) {
      throw new IllegalArgumentException("Database URL must not be blank");
    }
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Database username must not be blank");
    }
    if (password == null) {
      throw new IllegalArgumentException("Database password must not be null");
    }
    if (maxPoolSize < 1) {
      throw new IllegalArgumentException("Database max pool size must be at least 1");
    }
  }
}
