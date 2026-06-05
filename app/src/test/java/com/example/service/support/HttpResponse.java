package com.example.service.support;

import java.net.http.HttpHeaders;

/** Small HTTP response wrapper used by route tests. */
public record HttpResponse(int status, HttpHeaders headers, String body) {
  /** Returns the first header value for the supplied name, or null when absent. */
  public String header(String name) {
    return headers.firstValue(name).orElse(null);
  }
}
