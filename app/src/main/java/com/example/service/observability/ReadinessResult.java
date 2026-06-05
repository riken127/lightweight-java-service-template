package com.example.service.observability;

/** Result returned by a readiness dependency check. */
public record ReadinessResult(String name, boolean ready, String message) {
  /** Creates a successful readiness result. */
  public static ReadinessResult ready(String name) {
    return new ReadinessResult(name, true, "OK");
  }

  /** Creates a failed readiness result with a safe message. */
  public static ReadinessResult notReady(String name, String message) {
    return new ReadinessResult(name, false, message);
  }
}
