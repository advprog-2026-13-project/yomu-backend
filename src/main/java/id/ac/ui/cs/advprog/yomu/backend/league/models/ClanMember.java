package id.ac.ui.cs.advprog.yomu.backend.league.models;

import jakarta.persistence.*;
        import lombok.*;
        import java.time.LocalDateTime;

@Entity
@Table(
        name = "clan_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClanMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "clan_id", nullable = false)
    private String clanId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    @Builder.Default
    private double totalScore = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private double accuracy = 0.0; // 0.0 - 1.0

    @Column(nullable = false)
    @Builder.Default
    private int quizCompleted = 0;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();
}
