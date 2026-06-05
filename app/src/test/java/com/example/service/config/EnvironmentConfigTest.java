package com.example.service.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;

class EnvironmentConfigTest {
  @Test
  void usesLocalDefaultsWhenEnvironmentIsEmpty() {
    AppConfig config = EnvironmentConfig.load(Map.of());

    assertThat(config.serviceName()).isEqualTo("service-template");
    assertThat(config.server().port()).isEqualTo(8080);
    assertThat(config.grpc().port()).isEqualTo(9090);
    assertThat(config.database().url())
        .isEqualTo("jdbc:postgresql://localhost:5432/service_template");
    assertThat(config.database().username()).isEqualTo("service_template");
    assertThat(config.database().password()).isEqualTo("service_template");
    assertThat(config.database().maxPoolSize()).isEqualTo(5);
    assertThat(config.logLevel()).isEqualTo("INFO");
  }

  @Test
  void readsEnvironmentOverrides() {
    AppConfig config =
        EnvironmentConfig.load(
            Map.of(
                "SERVICE_NAME",
                "orders",
                "HTTP_PORT",
                "9090",
                "GRPC_PORT",
                "9191",
                "DATABASE_URL",
                "jdbc:postgresql://db:5432/orders",
                "DATABASE_USERNAME",
                "orders_user",
                "DATABASE_PASSWORD",
                "secret",
                "DATABASE_MAX_POOL_SIZE",
                "12",
                "LOG_LEVEL",
                "DEBUG"));

    assertThat(config.serviceName()).isEqualTo("orders");
    assertThat(config.server().port()).isEqualTo(9090);
    assertThat(config.grpc().port()).isEqualTo(9191);
    assertThat(config.database().url()).isEqualTo("jdbc:postgresql://db:5432/orders");
    assertThat(config.database().username()).isEqualTo("orders_user");
    assertThat(config.database().password()).isEqualTo("secret");
    assertThat(config.database().maxPoolSize()).isEqualTo(12);
    assertThat(config.logLevel()).isEqualTo("DEBUG");
  }

  @Test
  void rejectsInvalidIntegerValues() {
    assertThatThrownBy(() -> EnvironmentConfig.load(Map.of("HTTP_PORT", "invalid")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("HTTP_PORT must be an integer");
  }
}
