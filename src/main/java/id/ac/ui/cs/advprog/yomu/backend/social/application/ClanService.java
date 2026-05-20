package id.ac.ui.cs.advprog.yomu.backend.social.application;

import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.ClanNotFoundException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMemberRole;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClanService {

  private final ClanRepositoryPort clanRepository;
  private final ClanMemberRepositoryPort clanMemberRepository;

  public ClanService(
      ClanRepositoryPort clanRepository, ClanMemberRepositoryPort clanMemberRepository) {
    this.clanRepository = clanRepository;
    this.clanMemberRepository = clanMemberRepository;
  }

  public ClanResponse createClan(String clanName, UUID leaderId) {
    if (clanMemberRepository.existsByUserId(leaderId)) {
      throw new IllegalStateException("User is already a member of a clan");
    }
    if (clanRepository.existsByName(clanName)) {
      throw new IllegalArgumentException("Clan name is already taken");
    }

    Clan clan = Clan.createNew(clanName, leaderId);
    clan = clanRepository.save(clan);

    ClanMember leader = new ClanMember();
    leader.setClanId(clan.getId());
    leader.setUserId(leaderId);
    leader.setRole(ClanMemberRole.LEADER);
    clanMemberRepository.save(leader);

    return new ClanResponse(clan, 1L);
  }

  public ClanResponse joinClan(UUID clanId, UUID userId) {
    if (clanMemberRepository.existsByUserId(userId)) {
      throw new IllegalStateException("User is already a member of a clan");
    }
    Clan clan =
        clanRepository
            .findById(clanId)
            .orElseThrow(() -> new ClanNotFoundException("Clan not found"));

    ClanMember member = ClanMember.join(clanId, userId);
    clanMemberRepository.save(member);

    long memberCount = clanMemberRepository.countByClanId(clanId);
    return new ClanResponse(clan, memberCount);
  }

  public void leaveClan(UUID userId) {
    ClanMember member =
        clanMemberRepository
            .findByUserId(userId)
            .orElseThrow(() -> new ClanNotFoundException("User is not in a clan"));

    if (member.getRole() == ClanMemberRole.LEADER) {
      UUID clanId = member.getClanId();
      clanMemberRepository.deleteAll(clanMemberRepository.findByClanId(clanId));
      clanRepository.deleteById(clanId);
    } else {
      clanMemberRepository.delete(member);
    }
  }

  @Transactional(readOnly = true)
  public ClanResponse getClan(UUID clanId) {
    Clan clan =
        clanRepository
            .findById(clanId)
            .orElseThrow(() -> new ClanNotFoundException("Clan not found"));
    long memberCount = clanMemberRepository.countByClanId(clanId);
    return new ClanResponse(clan, memberCount);
  }

  @Transactional(readOnly = true)
  public Optional<ClanResponse> getMyClan(UUID userId) {
    return clanMemberRepository
        .findByUserId(userId)
        .map(
            m -> {
              Clan clan =
                  clanRepository
                      .findById(m.getClanId())
                      .orElseThrow(() -> new ClanNotFoundException("Clan not found"));
              long count = clanMemberRepository.countByClanId(m.getClanId());
              return new ClanResponse(clan, count);
            });
  }

  public void addScoreToMemberClan(UUID userId, long scoreToAdd) {
    clanMemberRepository
        .findByUserId(userId)
        .flatMap(member -> clanRepository.findById(member.getClanId()))
        .ifPresent(
            clan -> {
              clan.setScore(clan.getScore() + scoreToAdd);
              clanRepository.save(clan);
            });
  }
}
