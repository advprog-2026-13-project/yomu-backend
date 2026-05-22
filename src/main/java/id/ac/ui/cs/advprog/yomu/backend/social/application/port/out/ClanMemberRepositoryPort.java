package id.ac.ui.cs.advprog.yomu.backend.social.application.port.out;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClanMemberRepositoryPort {

  Optional<ClanMember> findByUserId(UUID userId);

  List<ClanMember> findByClanId(UUID clanId);

  boolean existsByUserId(UUID userId);

  long countByClanId(UUID clanId);

  ClanMember save(ClanMember member);

  void delete(ClanMember member);

  void deleteAll(Iterable<ClanMember> members);
}
