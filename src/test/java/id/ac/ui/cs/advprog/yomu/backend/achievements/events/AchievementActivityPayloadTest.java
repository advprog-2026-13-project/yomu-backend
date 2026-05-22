package id.ac.ui.cs.advprog.yomu.backend.achievements.events;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementActivityPayload;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementQuizCompletedPayload;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementReadingCompletedPayload;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AchievementActivityPayloadTest {

  @Test
  void readingPayloadShouldImplementAchievementActivityPayload() {
    UUID userId = UUID.randomUUID();
    AchievementReadingCompletedPayload payload =
        new AchievementReadingCompletedPayload(userId, UUID.randomUUID(), 120);

    assertInstanceOf(AchievementActivityPayload.class, payload);
    assertEquals(userId, ((AchievementActivityPayload) payload).getUserId());
  }

  @Test
  void quizPayloadShouldImplementAchievementActivityPayload() {
    UUID userId = UUID.randomUUID();
    AchievementQuizCompletedPayload payload =
        new AchievementQuizCompletedPayload(userId, UUID.randomUUID(), 100, true);

    assertInstanceOf(AchievementActivityPayload.class, payload);
    assertEquals(userId, ((AchievementActivityPayload) payload).getUserId());
  }

  @Test
  void payloadsShouldBeUsablePolymorphically() {
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();

    AchievementActivityPayload reading =
        new AchievementReadingCompletedPayload(userId1, UUID.randomUUID(), 60);
    AchievementActivityPayload quiz =
        new AchievementQuizCompletedPayload(userId2, UUID.randomUUID(), 90, true);

    // Both should be assignable to the interface without casting
    assertEquals(userId1, reading.getUserId());
    assertEquals(userId2, quiz.getUserId());
  }
}
