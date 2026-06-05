package com.example.service.http.dto;

import java.util.List;

/** JSON wrapper for the example item collection response. */
public record ExampleItemsResponse(List<ExampleItemResponse> items) {}
