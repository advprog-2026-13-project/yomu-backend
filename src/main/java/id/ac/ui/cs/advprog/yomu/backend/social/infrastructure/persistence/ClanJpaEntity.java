package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClanJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true, length = 50)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Tier tier;

  @Column(nullable = false)
  private long score;

  @Column(name = "leader_id", nullable = false)
  private UUID leaderId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  @SuppressWarnings("unused")
  void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
