package id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserDailyMissionProgressTest {

    private UUID progressId;
    private UUID userId;
    private UUID missionId;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        progressId = UUID.randomUUID();
        userId = UUID.randomUUID();
        missionId = UUID.randomUUID();
        date = LocalDate.now();
    }

    @Test
    void testCreation() {
        UserDailyMissionProgress progress = new UserDailyMissionProgress(progressId, userId, missionId, date);

        assertEquals(progressId, progress.getId());
        assertEquals(userId, progress.getUserId());
        assertEquals(missionId, progress.getMissionId());
        assertEquals(date, progress.getDate());
        assertEquals(0, progress.getCurrentProgress());
        assertFalse(progress.isCompleted());
    }

    @Test
    void testAddProgressCompletesIfReachesMilestone() {
        UserDailyMissionProgress progress = new UserDailyMissionProgress(progressId, userId, missionId, date);
        
        boolean justCompleted = progress.addProgress(2, 2);
        
        assertTrue(justCompleted);
        assertEquals(2, progress.getCurrentProgress());
        assertTrue(progress.isCompleted());
    }
}
