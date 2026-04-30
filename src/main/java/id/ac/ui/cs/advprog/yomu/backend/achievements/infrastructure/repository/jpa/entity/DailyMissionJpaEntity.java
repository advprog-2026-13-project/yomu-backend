package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "daily_missions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyMissionJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private AchievementType targetType;

    @Column(nullable = false)
    private int milestone;
}
