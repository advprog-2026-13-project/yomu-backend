package id.ac.ui.cs.advprog.yomu.backend.achievements.events;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementQuizCompletedPayload;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AchievementQuizPayloadTest {

  @Test
  void shouldCreateQuizPayloadWithExpectedFields() {
    UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    UUID quizId = UUID.fromString("11111111-2222-1111-1111-111111111111");
    int durationSec = 3;
    boolean completed = false;

    var payload =
        new AchievementQuizCompletedPayload(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            UUID.fromString("11111111-2222-1111-1111-111111111111"),
            3,
            false);

    assertEquals(userId, payload.getUserId());
    assertEquals(quizId, payload.getQuizId());
    assertEquals(durationSec, payload.getScore());
    assertEquals(completed, payload.isCompleted());
  }
}
