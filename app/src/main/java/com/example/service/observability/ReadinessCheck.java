package com.example.service.observability;

/** Checks whether an external dependency is ready for serving traffic. */
public interface ReadinessCheck {
  /** Returns the current readiness state for this dependency. */
  ReadinessResult check();
}
