package id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DailyMissionTest {

  @Test
  void testDailyMissionCreation() {
    UUID id = UUID.randomUUID();
    DailyMission mission =
        new DailyMission(id, "Misi Harian", "Selesaikan 1 Kuis", AchievementType.QUIZ_COMPLETED, 1);

    assertEquals(id, mission.getId());
    assertEquals("Misi Harian", mission.getName());
    assertEquals("Selesaikan 1 Kuis", mission.getDescription());
    assertEquals(AchievementType.QUIZ_COMPLETED, mission.getTargetType());
    assertEquals(1, mission.getMilestone());
  }

  @Test
  void testDailyMissionFailsWhenMilestoneInvalid() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new DailyMission(UUID.randomUUID(), "Test", "Test", AchievementType.READING_COMPLETED, 0);
        });
  }

  @Test
  void testUpdateAllFields() {
    UUID id = UUID.randomUUID();
    DailyMission original =
        new DailyMission(id, "Old Mission", "Old Desc", AchievementType.QUIZ_COMPLETED, 3);

    DailyMission updated = original.update("New Mission", "New Desc", 10);

    assertEquals(id, updated.getId());
    assertEquals("New Mission", updated.getName());
    assertEquals("New Desc", updated.getDescription());
    assertEquals(AchievementType.QUIZ_COMPLETED, updated.getTargetType());
    assertEquals(10, updated.getMilestone());
  }

  @Test
  void testUpdatePartialFieldsNullKeepsCurrent() {
    UUID id = UUID.randomUUID();
    DailyMission original =
        new DailyMission(id, "Keep Name", "Keep Desc", AchievementType.READING_COMPLETED, 5);

    DailyMission updated = original.update(null, null, null);

    assertEquals(id, updated.getId());
    assertEquals("Keep Name", updated.getName());
    assertEquals("Keep Desc", updated.getDescription());
    assertEquals(5, updated.getMilestone());
  }

  @Test
  void testUpdateOnlyMilestone() {
    UUID id = UUID.randomUUID();
    DailyMission original =
        new DailyMission(id, "Mission", "Desc", AchievementType.QUIZ_COMPLETED, 5);

    DailyMission updated = original.update(null, null, 15);

    assertEquals("Mission", updated.getName());
    assertEquals("Desc", updated.getDescription());
    assertEquals(15, updated.getMilestone());
  }

  @Test
  void testUpdateWithInvalidMilestoneThrows() {
    DailyMission original =
        new DailyMission(UUID.randomUUID(), "Test", "Desc", AchievementType.QUIZ_COMPLETED, 5);

    assertThrows(IllegalArgumentException.class, () -> original.update(null, null, 0));
    assertThrows(IllegalArgumentException.class, () -> original.update(null, null, -1));
  }

  @Test
  void testUpdatePreservesImmutability() {
    UUID id = UUID.randomUUID();
    DailyMission original =
        new DailyMission(id, "Original", "Desc", AchievementType.QUIZ_COMPLETED, 3);

    DailyMission updated = original.update("Updated", null, null);

    assertNotSame(original, updated);
    assertEquals("Original", original.getName());
    assertEquals("Updated", updated.getName());
  }
}
