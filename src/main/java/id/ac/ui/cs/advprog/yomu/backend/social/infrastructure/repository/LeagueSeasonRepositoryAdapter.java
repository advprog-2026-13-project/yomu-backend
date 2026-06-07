package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository;

import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.SeasonStatePort;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.LeagueSeasonJpaEntity;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository.jpa.SpringDataLeagueSeasonRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class LeagueSeasonRepositoryAdapter implements SeasonStatePort {

  private static final int SINGLETON_ID = 1;

  private final SpringDataLeagueSeasonRepository jpa;

  public LeagueSeasonRepositoryAdapter(SpringDataLeagueSeasonRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public LocalDateTime getCurrentSeasonStart() {
    return jpa.findById(SINGLETON_ID)
        .map(LeagueSeasonJpaEntity::getStartedAt)
        .orElse(LocalDateTime.MIN);
  }

  @Override
  public void startNewSeason(LocalDateTime startedAt) {
    LeagueSeasonJpaEntity entity =
        jpa.findById(SINGLETON_ID)
            .orElseGet(
                () -> {
                  LeagueSeasonJpaEntity fresh = new LeagueSeasonJpaEntity();
                  fresh.setId(SINGLETON_ID);
                  return fresh;
                });
    entity.setStartedAt(startedAt);
    jpa.save(entity);
  }
}
