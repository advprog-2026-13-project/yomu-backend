package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "league_season")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeagueSeasonJpaEntity {

  @Id private Integer id;

  @Column(name = "started_at", nullable = false)
  private LocalDateTime startedAt;
}
