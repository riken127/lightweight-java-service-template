package com.example.service.http.dto;

import java.time.Instant;
import java.util.UUID;

/** HTTP representation of an example item. */
public record ExampleItemResponse(UUID id, String name, Instant createdAt) {}
