package id.ac.ui.cs.advprog.yomu.backend.league.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Tier tier = Tier.BRONZE;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}