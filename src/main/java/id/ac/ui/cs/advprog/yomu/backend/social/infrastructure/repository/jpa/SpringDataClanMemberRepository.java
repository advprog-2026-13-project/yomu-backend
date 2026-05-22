package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository.jpa;

import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.ClanMemberJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataClanMemberRepository extends JpaRepository<ClanMemberJpaEntity, UUID> {

  Optional<ClanMemberJpaEntity> findByUserId(UUID userId);

  List<ClanMemberJpaEntity> findByClanId(UUID clanId);

  boolean existsByUserId(UUID userId);

  long countByClanId(UUID clanId);
}
