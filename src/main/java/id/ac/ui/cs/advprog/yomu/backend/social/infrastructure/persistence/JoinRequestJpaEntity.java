package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequestStatus;
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
@Table(name = "clan_join_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequestJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "clan_id", nullable = false)
  private UUID clanId;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private JoinRequestStatus status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "resolved_at")
  private Instant resolvedAt;

  @PrePersist
  @SuppressWarnings("unused")
  void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
