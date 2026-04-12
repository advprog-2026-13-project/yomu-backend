package id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class UserDailyMissionProgress {
    private final UUID id;
    private final UUID userId;
    private final UUID missionId;
    private final LocalDate date;
    private int currentProgress;
    private boolean isCompleted;
    private Instant completedAt;

    public UserDailyMissionProgress(UUID id, UUID userId, UUID missionId, LocalDate date) {
        this.id = id != null ? id : UUID.randomUUID();
        this.userId = userId;
        this.missionId = missionId;
        this.date = date != null ? date : LocalDate.now();
        this.currentProgress = 0;
        this.isCompleted = false;
    }

    public boolean addProgress(int amount, int milestoneTarget) {
        if (this.isCompleted) {
            return false;
        }

        this.currentProgress += amount;
        if (this.currentProgress >= milestoneTarget) {
            this.currentProgress = milestoneTarget;
            this.isCompleted = true;
            this.completedAt = Instant.now();
            return true;
        }
        
        return false;
    }
}
