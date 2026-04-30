package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "achievements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementJpaEntity {
  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "achievement_type", nullable = false)
  private AchievementType achievementType;

  @Column(nullable = false)
  private int milestone;
}
