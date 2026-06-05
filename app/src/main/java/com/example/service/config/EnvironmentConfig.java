package com.example.service.config;

/** Loads service configuration from environment variables with local defaults. */
public final class EnvironmentConfig {
  private EnvironmentConfig() {}

  /** Reads and validates configuration for application bootstrap. */
  public static AppConfig load() {
    return new AppConfig(
        string("SERVICE_NAME", "service-template"),
        new ServerConfig(integer("HTTP_PORT", 8080)),
        new DatabaseConfig(
            string("DATABASE_URL", "jdbc:postgresql://localhost:5432/service_template"),
            string("DATABASE_USERNAME", "service_template"),
            string("DATABASE_PASSWORD", "service_template"),
            integer("DATABASE_MAX_POOL_SIZE", 5)),
        string("LOG_LEVEL", "INFO"));
  }

  private static String string(String name, String defaultValue) {
    String value = System.getenv(name);
    return value == null || value.isBlank() ? defaultValue : value;
  }

  private static int integer(String name, int defaultValue) {
    String value = System.getenv(name);
    if (value == null || value.isBlank()) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(name + " must be an integer", e);
    }
  }
}
