package id.ac.ui.cs.advprog.yomu.backend.achievements.events;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.*;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementReadingCompletedPayload;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AchievementEnvelopeTest {

  static class AchievementTestEnvelope<T> extends AchievementEnvelope<T> {
    AchievementTestEnvelope(
        UUID eventId, AchievementType type, int version, Instant occurredAt, T payload) {
      super(eventId, type, version, occurredAt, payload);
    }
  }

  @Test
  void shouldCreateEnvelopeWithExpectedFields() {
    UUID achievementId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    Instant occurredAt = Instant.parse("2026-03-06T10:15:30Z");

    var payload =
        new AchievementReadingCompletedPayload(
            UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
            UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
            120);

    var env =
        new AchievementTestEnvelope<>(
            achievementId, AchievementType.READING_COMPLETED, 1, occurredAt, payload);

    assertEquals(achievementId, env.getAchievementId());
    assertEquals(AchievementType.READING_COMPLETED, env.getAchievementType());
    assertEquals(1, env.getVersion());
    assertEquals(occurredAt, env.getOccurredAt());
    assertSame(payload, env.getPayload());
  }
}
