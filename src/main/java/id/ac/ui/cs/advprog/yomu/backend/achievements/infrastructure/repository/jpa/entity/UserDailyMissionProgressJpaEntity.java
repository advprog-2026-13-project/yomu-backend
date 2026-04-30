package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_daily_mission_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDailyMissionProgressJpaEntity {
  @Id private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "mission_id", nullable = false)
  private UUID missionId;

  @Column(nullable = false)
  private LocalDate date;

  @Column(name = "current_progress", nullable = false)
  private int currentProgress;

  @Column(name = "is_completed", nullable = false)
  private boolean isCompleted;

  @Column(name = "completed_at")
  private Instant completedAt;
}
