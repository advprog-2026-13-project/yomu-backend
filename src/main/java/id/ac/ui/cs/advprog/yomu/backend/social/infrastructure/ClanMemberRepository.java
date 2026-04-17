package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClanMemberRepository extends JpaRepository<ClanMember, UUID> {
  Optional<ClanMember> findByUserId(UUID userId);

  List<ClanMember> findByClanId(UUID clanId);

  boolean existsByUserId(UUID userId);

  long countByClanId(UUID clanId);
}
