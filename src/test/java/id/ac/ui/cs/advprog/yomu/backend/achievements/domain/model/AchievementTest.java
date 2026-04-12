package id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AchievementTest {

    @Test
    void testAchievementCreation() {
        UUID id = UUID.randomUUID();
        Achievement achievement = new Achievement(id, "Pembaca Cepat", "Membaca 10 kali", AchievementType.READING_COMPLETED, 10);

        assertEquals(id, achievement.getId());
        assertEquals("Pembaca Cepat", achievement.getName());
        assertEquals("Membaca 10 kali", achievement.getDescription());
        assertEquals(AchievementType.READING_COMPLETED, achievement.getAchievementType());
        assertEquals(10, achievement.getMilestone());
    }

    @Test
    void testAchievementCreationFailsWhenMilestoneZeroOrNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Achievement(UUID.randomUUID(), "Test", "Test", AchievementType.READING_COMPLETED, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Achievement(UUID.randomUUID(), "Test", "Test", AchievementType.READING_COMPLETED, -5);
        });
    }
}
