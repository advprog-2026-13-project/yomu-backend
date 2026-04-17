package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClanRepository extends JpaRepository<Clan, UUID> {
  boolean existsByName(String name);

  List<Clan> findByTierOrderByScoreDesc(Tier tier);
}