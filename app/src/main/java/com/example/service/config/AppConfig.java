package com.example.service.config;

/** Top-level service configuration loaded from the environment. */
public record AppConfig(
    String serviceName,
    ServerConfig server,
    GrpcConfig grpc,
    DatabaseConfig database,
    String logLevel) {}
