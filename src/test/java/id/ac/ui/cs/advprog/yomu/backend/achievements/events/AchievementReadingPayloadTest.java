package id.ac.ui.cs.advprog.yomu.backend.achievements.events;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementReadingCompletedPayload;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AchievementReadingPayloadTest {

  @Test
  void shouldCreateReadingPayloadWithExpectedFields() {
    UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    UUID readingId = UUID.fromString("11111111-2222-1111-1111-111111111111");
    int durationSec = 3;

    var payload =
        new AchievementReadingCompletedPayload(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            UUID.fromString("11111111-2222-1111-1111-111111111111"),
            3);

    assertEquals(userId, payload.getUserId());
    assertEquals(readingId, payload.getReadingId());
    assertEquals(durationSec, payload.getDurationSec());
  }
}
