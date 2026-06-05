package com.example.service.http.dto;

/** JSON error response returned by the central HTTP error mapper. */
public record ErrorResponse(String code, String message, String requestId) {}
