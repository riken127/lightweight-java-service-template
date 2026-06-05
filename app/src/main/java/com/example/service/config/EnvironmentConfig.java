package com.example.service.config;

import java.util.Map;

/** Loads service configuration from environment variables with local defaults. */
public final class EnvironmentConfig {
  private EnvironmentConfig() {}

  /** Reads and validates configuration for application bootstrap. */
  public static AppConfig load() {
    return load(System.getenv());
  }

  static AppConfig load(Map<String, String> environment) {
    return new AppConfig(
        string(environment, "SERVICE_NAME", "service-template"),
        new ServerConfig(integer(environment, "HTTP_PORT", 8080)),
        new DatabaseConfig(
            string(
                environment, "DATABASE_URL", "jdbc:postgresql://localhost:5432/service_template"),
            string(environment, "DATABASE_USERNAME", "service_template"),
            string(environment, "DATABASE_PASSWORD", "service_template"),
            integer(environment, "DATABASE_MAX_POOL_SIZE", 5)),
        string(environment, "LOG_LEVEL", "INFO"));
  }

  private static String string(Map<String, String> environment, String name, String defaultValue) {
    String value = environment.get(name);
    return value == null || value.isBlank() ? defaultValue : value;
  }

  private static int integer(Map<String, String> environment, String name, int defaultValue) {
    String value = environment.get(name);
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
