package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMemberRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clan_members", uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClanMemberJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "clan_id", nullable = false)
  private UUID clanId;

  @Column(name = "user_id", nullable = false, unique = true)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ClanMemberRole role;

  @Column(name = "joined_at", nullable = false)
  private Instant joinedAt;

  @PrePersist
  @SuppressWarnings("unused")
  void onCreate() {
    if (joinedAt == null) {
      joinedAt = Instant.now();
    }
  }
}
