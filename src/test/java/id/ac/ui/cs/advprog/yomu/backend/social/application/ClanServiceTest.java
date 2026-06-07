package id.ac.ui.cs.advprog.yomu.backend.social.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.AlreadyInClanException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.ClanNotFoundException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.DuplicateClanNameException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanActivityProvider;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMemberRole;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ClanActivitySnapshot;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ClanModifierStatus;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ModifierResolver;
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

@ExtendWith(MockitoExtension.class)
class ClanServiceTest {

  @Mock private ClanRepositoryPort clanRepository;
  @Mock private ClanMemberRepositoryPort clanMemberRepository;
  @Mock private ClanActivityProvider activityProvider;
  @Mock private ModifierResolver modifierResolver;

  @InjectMocks private ClanService clanService;

  private UUID leaderId;
  private UUID clanId;
  private UUID otherUserId;

  @BeforeEach
  void setUp() {
    leaderId = UUID.randomUUID();
    clanId = UUID.randomUUID();
    otherUserId = UUID.randomUUID();

    lenient()
        .when(activityProvider.getActivity(any()))
        .thenReturn(new ClanActivitySnapshot(0.0, 1.0));
    lenient().when(modifierResolver.describe(any())).thenReturn(ClanModifierStatus.none());
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
    assertEquals(1L, response.getMemberCount());

    ArgumentCaptor<ClanMember> memberCaptor = ArgumentCaptor.forClass(ClanMember.class);
    verify(clanMemberRepository).save(memberCaptor.capture());
    ClanMember savedMember = memberCaptor.getValue();
    assertEquals(clanId, savedMember.getClanId());
    assertEquals(leaderId, savedMember.getUserId());
    assertEquals(ClanMemberRole.LEADER, savedMember.getRole());
  }

  @Test
  void leaveClan_asLeader_hardDeletesAllMembersAndClanItself() {
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
  void getClan_happyPath_returnsClanResponseWithMemberCount() {
    Clan clan = new Clan();
    clan.setId(clanId);
    clan.setName("Vipers");
    clan.setLeaderId(leaderId);
    clan.setTier(Tier.BRONZE);
    clan.setScore(0L);
    when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
    when(clanMemberRepository.countByClanId(clanId)).thenReturn(3L);

    ClanResponse response = clanService.getClan(clanId);

    assertEquals(clanId, response.getId());
    assertEquals("Vipers", response.getName());
    assertEquals(3L, response.getMemberCount());
  }

  @Test
  void getClan_clanNotFound_throwsClanNotFoundException() {
    when(clanRepository.findById(clanId)).thenReturn(Optional.empty());

    assertThrows(ClanNotFoundException.class, () -> clanService.getClan(clanId));
  }

  @Test
  void getMyClan_userInClan_returnsClanResponse() {
    ClanMember member = new ClanMember();
    member.setUserId(leaderId);
    member.setClanId(clanId);
    Clan clan = new Clan();
    clan.setId(clanId);
    clan.setName("Vipers");
    clan.setLeaderId(leaderId);
    clan.setTier(Tier.BRONZE);
    clan.setScore(0L);
    when(clanMemberRepository.findByUserId(leaderId)).thenReturn(Optional.of(member));
    when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
    when(clanMemberRepository.countByClanId(clanId)).thenReturn(2L);

    Optional<ClanResponse> result = clanService.getMyClan(leaderId);

    assertTrue(result.isPresent());
    assertEquals(clanId, result.get().getId());
    assertEquals(2L, result.get().getMemberCount());
  }

  @Test
  void getMyClan_userNotInAnyClan_returnsEmpty() {
    when(clanMemberRepository.findByUserId(leaderId)).thenReturn(Optional.empty());

    Optional<ClanResponse> result = clanService.getMyClan(leaderId);

    assertTrue(result.isEmpty());
    verify(clanRepository, never()).findById(any());
  }

  @Test
  void leaveClan_userNotInAnyClan_throwsClanNotFoundException() {
    when(clanMemberRepository.findByUserId(leaderId)).thenReturn(Optional.empty());

    assertThrows(ClanNotFoundException.class, () -> clanService.leaveClan(leaderId));
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

  @Test
  void createClan_duplicateName_throwsDuplicateClanNameException() {
    when(clanMemberRepository.existsByUserId(leaderId)).thenReturn(false);
    when(clanRepository.existsByName("Vipers")).thenReturn(true);

    assertThrows(
        DuplicateClanNameException.class, () -> clanService.createClan("Vipers", leaderId));
  }

  @Test
  void createClan_userAlreadyInClan_throwsAlreadyInClanException() {
    when(clanMemberRepository.existsByUserId(leaderId)).thenReturn(true);

    assertThrows(AlreadyInClanException.class, () -> clanService.createClan("Vipers", leaderId));
  }

  @Test
  void deleteClan_asLeader_deletesAllMembersAndClan() {
    Clan clan = new Clan();
    clan.setId(clanId);
    clan.setLeaderId(leaderId);
    ClanMember leader = new ClanMember();
    leader.setUserId(leaderId);
    leader.setClanId(clanId);
    leader.setRole(ClanMemberRole.LEADER);
    ClanMember peer = new ClanMember();
    peer.setUserId(otherUserId);
    peer.setClanId(clanId);
    peer.setRole(ClanMemberRole.MEMBER);
    List<ClanMember> allMembers = List.of(leader, peer);

    when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
    when(clanMemberRepository.findByClanId(clanId)).thenReturn(allMembers);

    clanService.deleteClan(clanId, leaderId);

    verify(clanMemberRepository).deleteAll(allMembers);
    verify(clanRepository).deleteById(clanId);
  }

  @Test
  void deleteClan_asNonLeader_throwsNotClanLeaderException() {
    Clan clan = new Clan();
    clan.setId(clanId);
    clan.setLeaderId(leaderId);

    when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));

    assertThrows(
        id.ac.ui.cs.advprog.yomu.backend.social.application.exception.NotClanLeaderException.class,
        () -> clanService.deleteClan(clanId, otherUserId));

    verify(clanMemberRepository, never()).deleteAll(anyIterable());
    verify(clanRepository, never()).deleteById(any(UUID.class));
  }

  @Test
  void deleteClan_clanNotFound_throwsClanNotFoundException() {
    when(clanRepository.findById(clanId)).thenReturn(Optional.empty());

    assertThrows(
        id.ac.ui.cs.advprog.yomu.backend.social.application.exception.ClanNotFoundException.class,
        () -> clanService.deleteClan(clanId, leaderId));

    verify(clanMemberRepository, never()).deleteAll(anyIterable());
    verify(clanRepository, never()).deleteById(any(UUID.class));
  }

  @Test
  void adminDeleteClan_clanExists_deletesAllMembersAndClan() {
    Clan clan = new Clan();
    clan.setId(clanId);
    clan.setLeaderId(leaderId);
    ClanMember member = new ClanMember();
    member.setUserId(leaderId);
    member.setClanId(clanId);
    List<ClanMember> members = List.of(member);
    when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
    when(clanMemberRepository.findByClanId(clanId)).thenReturn(members);

    clanService.adminDeleteClan(clanId);

    verify(clanMemberRepository).deleteAll(members);
    verify(clanRepository).deleteById(clanId);
  }

  @Test
  void adminDeleteClan_clanNotFound_throwsClanNotFoundException() {
    when(clanRepository.findById(clanId)).thenReturn(Optional.empty());

    assertThrows(
        id.ac.ui.cs.advprog.yomu.backend.social.application.exception.ClanNotFoundException.class,
        () -> clanService.adminDeleteClan(clanId));
  }

  @Test
  void adminRemoveMember_memberFound_deletesMember() {
    ClanMember member = new ClanMember();
    member.setUserId(otherUserId);
    member.setClanId(clanId);
    when(clanMemberRepository.findByUserId(otherUserId)).thenReturn(Optional.of(member));

    clanService.adminRemoveMember(clanId, otherUserId);

    verify(clanMemberRepository).delete(member);
  }

  @Test
  void adminRemoveMember_memberNotInThisClan_throwsClanNotFoundException() {
    ClanMember memberOfOtherClan = new ClanMember();
    memberOfOtherClan.setUserId(otherUserId);
    memberOfOtherClan.setClanId(UUID.randomUUID());
    when(clanMemberRepository.findByUserId(otherUserId)).thenReturn(Optional.of(memberOfOtherClan));

    assertThrows(
        id.ac.ui.cs.advprog.yomu.backend.social.application.exception.ClanNotFoundException.class,
        () -> clanService.adminRemoveMember(clanId, otherUserId));
  }

  @Test
  void addScoreToMemberClan_userNotInAnyClan_noOp() {
    when(clanMemberRepository.findByUserId(otherUserId)).thenReturn(Optional.empty());

    clanService.addScoreToMemberClan(otherUserId, 50L);

    verify(clanRepository, never()).save(any());
  }
}
