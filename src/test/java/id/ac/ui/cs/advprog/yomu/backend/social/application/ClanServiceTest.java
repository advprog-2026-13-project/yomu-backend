package id.ac.ui.cs.advprog.yomu.backend.social.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMemberRole;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.ClanMemberRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Characterization tests — pin behavior SAAT INI (termasuk yang audit-flagged 🟡) sebagai safety
 * net sebelum refactor 4B/4C. Komentar PIN-CURRENT menandai perilaku yang nantinya mau
 * direvisi di Gelombang 3.
 */
@ExtendWith(MockitoExtension.class)
class ClanServiceTest {

  @Mock private ClanRepositoryPort clanRepository;
  @Mock private ClanMemberRepository clanMemberRepository;

  @InjectMocks private ClanService clanService;

  private UUID leaderId;
  private UUID clanId;
  private UUID otherUserId;

  @BeforeEach
  void setUp() {
    leaderId = UUID.randomUUID();
    clanId = UUID.randomUUID();
    otherUserId = UUID.randomUUID();
  }

  @Test
  void createClan_happyPath_savesClanAndLeaderMembershipAndReturnsResponseWithCount1() {
    when(clanMemberRepository.existsByUserId(leaderId)).thenReturn(false);
    when(clanRepository.existsByName("Vipers")).thenReturn(false);
    when(clanRepository.save(any(Clan.class)))
        .thenAnswer(
            inv -> {
              Clan c = inv.getArgument(0);
              c.setId(clanId);
              return c;
            });

    ClanResponse response = clanService.createClan("Vipers", leaderId);

    assertEquals(clanId, response.getId());
    assertEquals("Vipers", response.getName());
    assertEquals(leaderId, response.getLeaderId());
    assertEquals(Tier.BRONZE, response.getTier());
    assertEquals(0L, response.getScore());
    // PIN-CURRENT (audit 🟢 #17): memberCount di-hardcode 1L tanpa query countByClanId.
    // Konsisten kalau leader baru, tapi inkonsisten dengan branch lain (joinClan, getClan)
    // yang re-query. Pertahankan dulu.
    assertEquals(1L, response.getMemberCount());

    ArgumentCaptor<ClanMember> memberCaptor = ArgumentCaptor.forClass(ClanMember.class);
    verify(clanMemberRepository).save(memberCaptor.capture());
    ClanMember savedMember = memberCaptor.getValue();
    assertEquals(clanId, savedMember.getClanId());
    assertEquals(leaderId, savedMember.getUserId());
    assertEquals(ClanMemberRole.LEADER, savedMember.getRole());
  }

  @Test
  void joinClan_happyPath_savesMemberAndReturnsResponseWithFreshCount() {
    when(clanMemberRepository.existsByUserId(otherUserId)).thenReturn(false);
    Clan existing = new Clan();
    existing.setId(clanId);
    existing.setName("Vipers");
    existing.setLeaderId(leaderId);
    when(clanRepository.findById(clanId)).thenReturn(Optional.of(existing));
    when(clanMemberRepository.countByClanId(clanId)).thenReturn(2L);

    ClanResponse response = clanService.joinClan(clanId, otherUserId);

    assertEquals(clanId, response.getId());
    assertEquals("Vipers", response.getName());
    assertEquals(2L, response.getMemberCount());

    ArgumentCaptor<ClanMember> memberCaptor = ArgumentCaptor.forClass(ClanMember.class);
    verify(clanMemberRepository).save(memberCaptor.capture());
    ClanMember saved = memberCaptor.getValue();
    assertEquals(clanId, saved.getClanId());
    assertEquals(otherUserId, saved.getUserId());
    assertEquals(ClanMemberRole.MEMBER, saved.getRole());
  }

  @Test
  void leaveClan_asLeader_hardDeletesAllMembersAndClanItself() {
    // PIN-CURRENT (audit 🟡): leader leave => hard-delete semua member + Clan. Tidak ada
    // transfer ownership, tidak ada warning, tidak publish event. Behavior implicit.
    // Mau di-revisit di Gelombang 3 (auto-transfer / disband flow eksplisit).
    ClanMember leader = new ClanMember();
    leader.setUserId(leaderId);
    leader.setClanId(clanId);
    leader.setRole(ClanMemberRole.LEADER);
    ClanMember peer = new ClanMember();
    peer.setUserId(otherUserId);
    peer.setClanId(clanId);
    peer.setRole(ClanMemberRole.MEMBER);
    List<ClanMember> all = List.of(leader, peer);

    when(clanMemberRepository.findByUserId(leaderId)).thenReturn(Optional.of(leader));
    when(clanMemberRepository.findByClanId(clanId)).thenReturn(all);

    clanService.leaveClan(leaderId);

    verify(clanMemberRepository).deleteAll(all);
    verify(clanRepository).deleteById(clanId);
    verify(clanMemberRepository, never()).delete(any(ClanMember.class));
  }

  @Test
  void leaveClan_asMember_deletesOnlyOwnMembership() {
    ClanMember member = new ClanMember();
    member.setUserId(otherUserId);
    member.setClanId(clanId);
    member.setRole(ClanMemberRole.MEMBER);
    when(clanMemberRepository.findByUserId(otherUserId)).thenReturn(Optional.of(member));

    clanService.leaveClan(otherUserId);

    verify(clanMemberRepository).delete(member);
    verify(clanRepository, never()).deleteById(any(UUID.class));
    verify(clanMemberRepository, never()).deleteAll(anyIterable());
  }

  @Test
  void addScoreToMemberClan_userInClan_addsScoreToClanAndSaves() {
    ClanMember member = new ClanMember();
    member.setUserId(otherUserId);
    member.setClanId(clanId);
    Clan clan = new Clan();
    clan.setId(clanId);
    clan.setScore(100L);
    when(clanMemberRepository.findByUserId(otherUserId)).thenReturn(Optional.of(member));
    when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));

    clanService.addScoreToMemberClan(otherUserId, 50L);

    assertEquals(150L, clan.getScore());
    verify(clanRepository).save(clan);
  }
}
