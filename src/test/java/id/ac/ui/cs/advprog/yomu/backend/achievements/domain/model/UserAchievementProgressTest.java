package id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserAchievementProgressTest {

    private UUID progressId;
    private UUID userId;
    private UUID achievementId;

    @BeforeEach
    void setUp() {
        progressId = UUID.randomUUID();
        userId = UUID.randomUUID();
        achievementId = UUID.randomUUID();
    }

    @Test
    void testCreation() {
        UserAchievementProgress progress = new UserAchievementProgress(progressId, userId, achievementId);

        assertEquals(progressId, progress.getId());
        assertEquals(userId, progress.getUserId());
        assertEquals(achievementId, progress.getAchievementId());
        assertEquals(0, progress.getCurrentProgress());
        assertFalse(progress.isCompleted());
        assertNull(progress.getCompletedAt());
        assertFalse(progress.isDisplayedOnProfile());
    }

    @Test
    void testAddProgressDoesNotCompleteIfBelowMilestone() {
        UserAchievementProgress progress = new UserAchievementProgress(progressId, userId, achievementId);
        
        boolean justCompleted = progress.addProgress(5, 10);
        
        assertFalse(justCompleted);
        assertEquals(5, progress.getCurrentProgress());
        assertFalse(progress.isCompleted());
        assertNull(progress.getCompletedAt());
    }

    @Test
    void testAddProgressCompletesIfReachesMilestone() {
        UserAchievementProgress progress = new UserAchievementProgress(progressId, userId, achievementId);
        
        boolean justCompleted = progress.addProgress(10, 10);
        
        assertTrue(justCompleted);
        assertEquals(10, progress.getCurrentProgress());
        assertTrue(progress.isCompleted());
        assertNotNull(progress.getCompletedAt());
    }

    @Test
    void testAddProgressIgnoresIfAlreadyCompleted() {
        UserAchievementProgress progress = new UserAchievementProgress(progressId, userId, achievementId);
        progress.addProgress(10, 10); // Completed

        boolean justCompletedAgain = progress.addProgress(5, 10);
        
        assertFalse(justCompletedAgain);
        assertEquals(10, progress.getCurrentProgress()); // Should not increase
    }

    @Test
    void testSetDisplayedOnProfileOnlyWhenCompleted() {
        UserAchievementProgress progress = new UserAchievementProgress(progressId, userId, achievementId);
        
        assertThrows(IllegalStateException.class, () -> progress.setDisplayedOnProfile(true));

        progress.addProgress(10, 10);
        assertDoesNotThrow(() -> progress.setDisplayedOnProfile(true));
        assertTrue(progress.isDisplayedOnProfile());
    }
}
