package id.ac.ui.cs.advprog.yomu.backend.social.application;

import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.AlreadyInClanException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.ClanNotFoundException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.DuplicateClanNameException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.NotClanLeaderException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanActivityProvider;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMemberRole;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ClanModifierStatus;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ModifierResolver;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClanService {

  private final ClanRepositoryPort clanRepository;
  private final ClanMemberRepositoryPort clanMemberRepository;
  private final ClanActivityProvider activityProvider;
  private final ModifierResolver modifierResolver;

  public ClanService(
      ClanRepositoryPort clanRepository,
      ClanMemberRepositoryPort clanMemberRepository,
      ClanActivityProvider activityProvider,
      ModifierResolver modifierResolver) {
    this.clanRepository = clanRepository;
    this.clanMemberRepository = clanMemberRepository;
    this.activityProvider = activityProvider;
    this.modifierResolver = modifierResolver;
  }

  private ClanModifierStatus modifiersFor(UUID clanId) {
    return modifierResolver.describe(activityProvider.getActivity(clanId));
  }

  public ClanResponse createClan(String clanName, UUID leaderId) {
    if (clanMemberRepository.existsByUserId(leaderId)) {
      throw new AlreadyInClanException("User is already a member of a clan");
    }
    if (clanRepository.existsByName(clanName)) {
      throw new DuplicateClanNameException("Clan name is already taken");
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
    return new ClanResponse(clan, memberCount, modifiersFor(clanId));
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
              return new ClanResponse(clan, count, modifiersFor(m.getClanId()));
            });
  }

  public void deleteClan(UUID clanId, UUID callerId) {
    Clan clan =
        clanRepository
            .findById(clanId)
            .orElseThrow(() -> new ClanNotFoundException("Clan not found"));

    if (!clan.getLeaderId().equals(callerId)) {
      throw new NotClanLeaderException("Only the clan leader can delete the clan");
    }

    clanMemberRepository.deleteAll(clanMemberRepository.findByClanId(clanId));
    clanRepository.deleteById(clanId);
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

  public void adminDeleteClan(UUID clanId) {
    clanRepository.findById(clanId).orElseThrow(() -> new ClanNotFoundException("Clan not found"));
    clanMemberRepository.deleteAll(clanMemberRepository.findByClanId(clanId));
    clanRepository.deleteById(clanId);
  }

  public void adminRemoveMember(UUID clanId, UUID userId) {
    var member =
        clanMemberRepository
            .findByUserId(userId)
            .filter(m -> m.getClanId().equals(clanId))
            .orElseThrow(() -> new ClanNotFoundException("Member not found in this clan"));
    clanMemberRepository.delete(member);
  }
}
