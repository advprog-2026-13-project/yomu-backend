package id.ac.ui.cs.advprog.yomu.backend.social.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "clan_members",
    uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class ClanMember {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "clan_id", nullable = false)
  private UUID clanId;

  @Column(name = "user_id", nullable = false, unique = true)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ClanMemberRole role = ClanMemberRole.MEMBER;

  @Column(name = "joined_at", nullable = false)
  private Instant joinedAt = Instant.now();
}