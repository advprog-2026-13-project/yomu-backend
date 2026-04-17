package id.ac.ui.cs.advprog.yomu.backend.social.application;

import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMemberRole;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.ClanRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClanService {

  private final ClanRepository clanRepository;
  private final ClanMemberRepository clanMemberRepository;

  public ClanService(ClanRepository clanRepository, ClanMemberRepository clanMemberRepository) {
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

    Clan clan = new Clan();
    clan.setName(clanName);
    clan.setLeaderId(leaderId);
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
            .orElseThrow(() -> new EntityNotFoundException("Clan not found"));

    ClanMember member = new ClanMember();
    member.setClanId(clanId);
    member.setUserId(userId);
    member.setRole(ClanMemberRole.MEMBER);
    clanMemberRepository.save(member);

    long memberCount = clanMemberRepository.countByClanId(clanId);
    return new ClanResponse(clan, memberCount);
  }

  public void leaveClan(UUID userId) {
    ClanMember member =
        clanMemberRepository
            .findByUserId(userId)
            .orElseThrow(() -> new EntityNotFoundException("User is not in a clan"));

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
            .orElseThrow(() -> new EntityNotFoundException("Clan not found"));
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
                      .orElseThrow(() -> new EntityNotFoundException("Clan not found"));
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