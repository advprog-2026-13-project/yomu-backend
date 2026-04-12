package id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserAchievementProgress {
    private final UUID id;
    private final UUID userId;
    private final UUID achievementId;
    private int currentProgress;
    private boolean isCompleted;
    private Instant completedAt;
    private boolean isDisplayedOnProfile;

    public UserAchievementProgress(UUID id, UUID userId, UUID achievementId) {
        this.id = id != null ? id : UUID.randomUUID();
        this.userId = userId;
        this.achievementId = achievementId;
        this.currentProgress = 0;
        this.isCompleted = false;
        this.isDisplayedOnProfile = false;
    }

    public boolean addProgress(int amount, int milestoneTarget) {
        if (this.isCompleted) {
            return false;
        }

        this.currentProgress += amount;
        if (this.currentProgress >= milestoneTarget) {
            this.currentProgress = milestoneTarget; // Cap at milestone target just in case
            this.isCompleted = true;
            this.completedAt = Instant.now();
            return true; // indicates it just completed recently
        }
        
        return false;
    }

    public void setDisplayedOnProfile(boolean displayed) {
        if (displayed && !this.isCompleted) {
            throw new IllegalStateException("Cannot display incomplete achievement on profile");
        }
        this.isDisplayedOnProfile = displayed;
    }
}
