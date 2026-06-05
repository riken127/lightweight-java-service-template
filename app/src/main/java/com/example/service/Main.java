package com.example.service;

import com.example.service.bootstrap.Application;
import com.example.service.bootstrap.ApplicationBootstrap;
import com.example.service.config.AppConfig;
import com.example.service.config.EnvironmentConfig;

/** Process entry point for the service template application. */
public final class Main {
  private Main() {}

  /** Loads configuration, creates the application, and starts the server. */
  public static void main(String[] args) {
    AppConfig config = EnvironmentConfig.load();
    Application application = ApplicationBootstrap.create(config);
    application.start();
  }
}
