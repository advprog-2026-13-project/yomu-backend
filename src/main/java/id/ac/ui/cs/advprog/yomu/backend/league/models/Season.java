package id.ac.ui.cs.advprog.yomu.backend.league.models;

import jakarta.persistence.*;
        import lombok.*;
        import java.time.LocalDateTime;

@Entity
@Table(name = "seasons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name; // contoh: "Season 1"

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = false;
}
