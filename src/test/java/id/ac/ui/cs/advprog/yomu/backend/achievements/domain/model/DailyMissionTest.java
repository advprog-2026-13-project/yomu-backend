package id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DailyMissionTest {

    @Test
    void testDailyMissionCreation() {
        UUID id = UUID.randomUUID();
        DailyMission mission = new DailyMission(id, "Misi Harian", "Selesaikan 1 Kuis", AchievementType.QUIZ_COMPLETED, 1);

        assertEquals(id, mission.getId());
        assertEquals("Misi Harian", mission.getName());
        assertEquals("Selesaikan 1 Kuis", mission.getDescription());
        assertEquals(AchievementType.QUIZ_COMPLETED, mission.getTargetType());
        assertEquals(1, mission.getMilestone());
    }

    @Test
    void testDailyMissionFailsWhenMilestoneInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DailyMission(UUID.randomUUID(), "Test", "Test", AchievementType.READING_COMPLETED, 0);
        });
    }
}
