package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository.jpa;

import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.LeagueSeasonJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataLeagueSeasonRepository
    extends JpaRepository<LeagueSeasonJpaEntity, Integer> {}
