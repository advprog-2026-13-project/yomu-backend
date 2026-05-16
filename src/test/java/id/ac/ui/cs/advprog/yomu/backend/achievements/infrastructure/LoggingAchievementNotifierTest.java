package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IAchievementNotifier;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LoggingAchievementNotifierTest {

  private final LoggingAchievementNotifier notifier = new LoggingAchievementNotifier();

  @Test
  void shouldImplementIAchievementNotifier() {
    assertInstanceOf(IAchievementNotifier.class, notifier);
  }

  @Test
  void notifyAchievementUnlockedShouldNotThrow() {
    UUID userId = UUID.randomUUID();
    Achievement achievement =
        new Achievement(
            UUID.randomUUID(), "Pembaca Handal", "Baca 10 kali", AchievementType.READING_COMPLETED, 10);

    assertDoesNotThrow(() -> notifier.notifyAchievementUnlocked(userId, achievement));
  }

  @Test
  void notifyDailyMissionCompletedShouldNotThrow() {
    UUID userId = UUID.randomUUID();
    DailyMission mission =
        new DailyMission(
            UUID.randomUUID(), "Baca Harian", "Baca 3 kali", AchievementType.READING_COMPLETED, 3);

    assertDoesNotThrow(() -> notifier.notifyDailyMissionCompleted(userId, mission));
  }
}
