package com.example.service.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.service.domain.ExampleItem;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ExampleServiceTest {
  @Test
  void usesDefaultLimitWhenNoneIsRequested() {
    RecordingRepository repository = new RecordingRepository();
    ExampleService service = new ExampleService(repository);

    service.recentItems(null);

    assertThat(repository.lastLimit).isEqualTo(20);
  }

  @Test
  void capsLargeLimits() {
    RecordingRepository repository = new RecordingRepository();
    ExampleService service = new ExampleService(repository);

    service.recentItems(500);

    assertThat(repository.lastLimit).isEqualTo(100);
  }

  @Test
  void rejectsNonPositiveLimits() {
    ExampleService service = new ExampleService(limit -> List.of());

    assertThatThrownBy(() -> service.recentItems(0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("limit must be greater than zero");
  }

  private static final class RecordingRepository implements ExampleItemRepository {
    private int lastLimit;

    @Override
    public List<ExampleItem> findRecent(int limit) {
      this.lastLimit = limit;
      return List.of(new ExampleItem(UUID.randomUUID(), "template", Instant.now()));
    }
  }
}
