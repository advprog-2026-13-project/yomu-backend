package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_achievement_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievementProgressJpaEntity {
    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "achievement_id", nullable = false)
    private UUID achievementId;

    @Column(name = "current_progress", nullable = false)
    private int currentProgress;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "is_displayed_on_profile", nullable = false)
    private boolean isDisplayedOnProfile;
}
