package id.ac.ui.cs.advprog.yomu.backend.social.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clans")
@Getter
@Setter
@NoArgsConstructor
public class Clan {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true, length = 50)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Tier tier = Tier.BRONZE;

  @Column(nullable = false)
  private long score = 0;

  @Column(name = "leader_id", nullable = false)
  private UUID leaderId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();
}