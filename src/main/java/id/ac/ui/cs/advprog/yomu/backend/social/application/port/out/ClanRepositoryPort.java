package id.ac.ui.cs.advprog.yomu.backend.social.application.port.out;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClanRepositoryPort {

  boolean existsByName(String name);

  Optional<Clan> findById(UUID id);

  Clan save(Clan clan);

  void deleteById(UUID id);

  List<Clan> findByTierOrderByScoreDesc(Tier tier);
}
