package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository.jpa;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.ClanJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataClanRepository extends JpaRepository<ClanJpaEntity, UUID> {

  boolean existsByName(String name);

  List<ClanJpaEntity> findByTierOrderByScoreDesc(Tier tier);
}
