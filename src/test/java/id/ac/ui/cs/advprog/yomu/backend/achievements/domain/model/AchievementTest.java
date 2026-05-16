package id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AchievementTest {

  @Test
  void testAchievementCreation() {
    UUID id = UUID.randomUUID();
    Achievement achievement =
        new Achievement(
            id, "Pembaca Cepat", "Membaca 10 kali", AchievementType.READING_COMPLETED, 10);

    assertEquals(id, achievement.getId());
    assertEquals("Pembaca Cepat", achievement.getName());
    assertEquals("Membaca 10 kali", achievement.getDescription());
    assertEquals(AchievementType.READING_COMPLETED, achievement.getAchievementType());
    assertEquals(10, achievement.getMilestone());
  }

  @Test
  void testAchievementCreationFailsWhenMilestoneZeroOrNegative() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new Achievement(UUID.randomUUID(), "Test", "Test", AchievementType.READING_COMPLETED, 0);
        });

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new Achievement(UUID.randomUUID(), "Test", "Test", AchievementType.READING_COMPLETED, -5);
        });
  }

  @Test
  void testUpdateAllFields() {
    UUID id = UUID.randomUUID();
    Achievement original =
        new Achievement(id, "Old Name", "Old Desc", AchievementType.READING_COMPLETED, 10);

    Achievement updated = original.update("New Name", "New Desc", 20);

    assertEquals(id, updated.getId());
    assertEquals("New Name", updated.getName());
    assertEquals("New Desc", updated.getDescription());
    assertEquals(AchievementType.READING_COMPLETED, updated.getAchievementType());
    assertEquals(20, updated.getMilestone());
  }

  @Test
  void testUpdatePartialFieldsNullKeepsCurrent() {
    UUID id = UUID.randomUUID();
    Achievement original =
        new Achievement(id, "Keep Name", "Keep Desc", AchievementType.QUIZ_COMPLETED, 5);

    Achievement updated = original.update(null, null, null);

    assertEquals(id, updated.getId());
    assertEquals("Keep Name", updated.getName());
    assertEquals("Keep Desc", updated.getDescription());
    assertEquals(AchievementType.QUIZ_COMPLETED, updated.getAchievementType());
    assertEquals(5, updated.getMilestone());
  }

  @Test
  void testUpdateOnlyName() {
    UUID id = UUID.randomUUID();
    Achievement original =
        new Achievement(id, "Old", "Desc", AchievementType.READING_COMPLETED, 10);

    Achievement updated = original.update("New", null, null);

    assertEquals("New", updated.getName());
    assertEquals("Desc", updated.getDescription());
    assertEquals(10, updated.getMilestone());
  }

  @Test
  void testUpdateWithInvalidMilestoneThrows() {
    Achievement original =
        new Achievement(UUID.randomUUID(), "Test", "Desc", AchievementType.READING_COMPLETED, 10);

    assertThrows(IllegalArgumentException.class, () -> original.update(null, null, 0));
    assertThrows(IllegalArgumentException.class, () -> original.update(null, null, -1));
  }

  @Test
  void testUpdatePreservesImmutability() {
    UUID id = UUID.randomUUID();
    Achievement original =
        new Achievement(id, "Original", "Desc", AchievementType.READING_COMPLETED, 10);

    Achievement updated = original.update("Updated", null, null);

    assertNotSame(original, updated);
    assertEquals("Original", original.getName());
    assertEquals("Updated", updated.getName());
  }
}

