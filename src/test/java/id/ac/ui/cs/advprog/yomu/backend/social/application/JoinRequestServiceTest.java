package id.ac.ui.cs.advprog.yomu.backend.social.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.AlreadyMemberException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.ClanNotFoundException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.DuplicateJoinRequestException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.JoinRequestAlreadyResolvedException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.NotClanLeaderException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.JoinRequestRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequest;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequestStatus;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JoinRequestServiceTest {

  private static final UUID CLAN_ID = UUID.randomUUID();
  private static final UUID LEADER_ID = UUID.randomUUID();
  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID REQUEST_ID = UUID.randomUUID();

  @Mock private ClanRepositoryPort clanRepository;
  @Mock private ClanMemberRepositoryPort clanMemberRepository;
  @Mock private JoinRequestRepositoryPort joinRequestRepository;

  @InjectMocks private JoinRequestService joinRequestService;

  private Clan clan;

  @BeforeEach
  void setUp() {
    clan = new Clan();
    clan.setId(CLAN_ID);
    clan.setName("TestClan");
    clan.setTier(Tier.BRONZE);
    clan.setLeaderId(LEADER_ID);

    lenient().when(clanRepository.findById(CLAN_ID)).thenReturn(Optional.of(clan));
    lenient().when(clanMemberRepository.existsByUserId(any())).thenReturn(false);
    lenient()
        .when(
            joinRequestRepository.existsByClanIdAndUserIdAndStatus(
                any(), any(), eq(JoinRequestStatus.PENDING)))
        .thenReturn(false);
    lenient()
        .when(joinRequestRepository.save(any(JoinRequest.class)))
        .thenAnswer(inv -> inv.getArgument(0));
    lenient()
        .when(clanMemberRepository.save(any(ClanMember.class)))
        .thenAnswer(inv -> inv.getArgument(0));
  }

  @Test
  void submitJoinRequest_happyPath_returnsPendingRequest() {
    JoinRequest result = joinRequestService.submitJoinRequest(CLAN_ID, USER_ID);

    assertEquals(JoinRequestStatus.PENDING, result.getStatus());
    assertEquals(CLAN_ID, result.getClanId());
    assertEquals(USER_ID, result.getUserId());
    assertNotNull(result.getCreatedAt());
    assertNull(result.getResolvedAt());
    verify(joinRequestRepository).save(any(JoinRequest.class));
  }

  @Test
  void submitJoinRequest_clanNotFound_throwsClanNotFoundException() {
    when(clanRepository.findById(CLAN_ID)).thenReturn(Optional.empty());

    assertThrows(
        ClanNotFoundException.class, () -> joinRequestService.submitJoinRequest(CLAN_ID, USER_ID));
    verify(joinRequestRepository, never()).save(any());
  }

  @Test
  void submitJoinRequest_userAlreadyMember_throwsAlreadyMemberException() {
    when(clanMemberRepository.existsByUserId(USER_ID)).thenReturn(true);

    assertThrows(
        AlreadyMemberException.class, () -> joinRequestService.submitJoinRequest(CLAN_ID, USER_ID));
    verify(joinRequestRepository, never()).save(any());
  }

  @Test
  void submitJoinRequest_duplicatePendingExists_throwsDuplicateJoinRequestException() {
    when(joinRequestRepository.existsByClanIdAndUserIdAndStatus(
            CLAN_ID, USER_ID, JoinRequestStatus.PENDING))
        .thenReturn(true);

    assertThrows(
        DuplicateJoinRequestException.class,
        () -> joinRequestService.submitJoinRequest(CLAN_ID, USER_ID));
    verify(joinRequestRepository, never()).save(any());
  }

  @Test
  void listPendingRequests_asLeader_returnsPendingList() {
    JoinRequest pending = JoinRequest.create(CLAN_ID, USER_ID);
    when(joinRequestRepository.findByClanIdAndStatus(CLAN_ID, JoinRequestStatus.PENDING))
        .thenReturn(List.of(pending));

    List<JoinRequest> result = joinRequestService.listPendingRequests(CLAN_ID, LEADER_ID);

    assertEquals(1, result.size());
    assertSame(pending, result.get(0));
  }

  @Test
  void listPendingRequests_asNonLeader_throwsNotClanLeaderException() {
    UUID nonLeader = UUID.randomUUID();

    assertThrows(
        NotClanLeaderException.class,
        () -> joinRequestService.listPendingRequests(CLAN_ID, nonLeader));
    verify(joinRequestRepository, never()).findByClanIdAndStatus(any(), any());
  }

  @Test
  void approveRequest_asLeader_approvesRequestAndCreatesMember() {
    JoinRequest pending = JoinRequest.create(CLAN_ID, USER_ID);
    pending.setId(REQUEST_ID);
    when(joinRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(pending));

    joinRequestService.approveRequest(CLAN_ID, REQUEST_ID, LEADER_ID);

    assertEquals(JoinRequestStatus.APPROVED, pending.getStatus());
    assertNotNull(pending.getResolvedAt());
    verify(clanMemberRepository).save(any(ClanMember.class));
    verify(joinRequestRepository).save(pending);
  }

  @Test
  void approveRequest_asNonLeader_throwsNotClanLeaderException() {
    UUID nonLeader = UUID.randomUUID();
    JoinRequest pending = JoinRequest.create(CLAN_ID, USER_ID);
    pending.setId(REQUEST_ID);
    when(joinRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(pending));

    assertThrows(
        NotClanLeaderException.class,
        () -> joinRequestService.approveRequest(CLAN_ID, REQUEST_ID, nonLeader));
    verify(clanMemberRepository, never()).save(any());
  }

  @Test
  void approveRequest_alreadyApproved_throwsJoinRequestAlreadyResolvedException() {
    JoinRequest alreadyApproved = JoinRequest.create(CLAN_ID, USER_ID);
    alreadyApproved.setId(REQUEST_ID);
    alreadyApproved.approve();
    when(joinRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(alreadyApproved));

    assertThrows(
        JoinRequestAlreadyResolvedException.class,
        () -> joinRequestService.approveRequest(CLAN_ID, REQUEST_ID, LEADER_ID));
    verify(clanMemberRepository, never()).save(any());
  }

  @Test
  void rejectRequest_asLeader_rejectsRequestWithoutCreatingMember() {
    JoinRequest pending = JoinRequest.create(CLAN_ID, USER_ID);
    pending.setId(REQUEST_ID);
    when(joinRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(pending));

    joinRequestService.rejectRequest(CLAN_ID, REQUEST_ID, LEADER_ID);

    assertEquals(JoinRequestStatus.REJECTED, pending.getStatus());
    assertNotNull(pending.getResolvedAt());
    verify(joinRequestRepository).save(pending);
    verify(clanMemberRepository, never()).save(any());
  }

  @Test
  void approveRequest_userAlreadyMember_throwsAlreadyMemberException() {
    JoinRequest pending = JoinRequest.create(CLAN_ID, USER_ID);
    pending.setId(REQUEST_ID);
    when(joinRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(pending));
    when(clanMemberRepository.existsByUserId(USER_ID)).thenReturn(true);

    assertThrows(
        AlreadyMemberException.class,
        () -> joinRequestService.approveRequest(CLAN_ID, REQUEST_ID, LEADER_ID));
    verify(clanMemberRepository, never()).save(any());
    verify(joinRequestRepository, never()).save(any());
  }

  @Test
  void rejectRequest_asNonLeader_throwsNotClanLeaderException() {
    UUID nonLeader = UUID.randomUUID();
    JoinRequest pending = JoinRequest.create(CLAN_ID, USER_ID);
    pending.setId(REQUEST_ID);
    when(joinRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(pending));

    assertThrows(
        NotClanLeaderException.class,
        () -> joinRequestService.rejectRequest(CLAN_ID, REQUEST_ID, nonLeader));
    verify(joinRequestRepository, never()).save(any());
  }

  @Test
  void approveRequest_clanNotFound_throwsClanNotFoundException() {
    when(clanRepository.findById(CLAN_ID)).thenReturn(Optional.empty());

    assertThrows(
        ClanNotFoundException.class,
        () -> joinRequestService.approveRequest(CLAN_ID, REQUEST_ID, LEADER_ID));
  }

  @Test
  void approveRequest_requestNotFound_throwsClanNotFoundException() {
    when(joinRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

    assertThrows(
        ClanNotFoundException.class,
        () -> joinRequestService.approveRequest(CLAN_ID, REQUEST_ID, LEADER_ID));
  }

  @Test
  void rejectRequest_clanNotFound_throwsClanNotFoundException() {
    when(clanRepository.findById(CLAN_ID)).thenReturn(Optional.empty());

    assertThrows(
        ClanNotFoundException.class,
        () -> joinRequestService.rejectRequest(CLAN_ID, REQUEST_ID, LEADER_ID));
  }

  @Test
  void rejectRequest_requestNotFound_throwsClanNotFoundException() {
    when(joinRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

    assertThrows(
        ClanNotFoundException.class,
        () -> joinRequestService.rejectRequest(CLAN_ID, REQUEST_ID, LEADER_ID));
  }

  @Test
  void rejectRequest_alreadyRejected_throwsJoinRequestAlreadyResolvedException() {
    JoinRequest alreadyRejected = JoinRequest.create(CLAN_ID, USER_ID);
    alreadyRejected.setId(REQUEST_ID);
    alreadyRejected.reject();
    when(joinRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(alreadyRejected));

    assertThrows(
        JoinRequestAlreadyResolvedException.class,
        () -> joinRequestService.rejectRequest(CLAN_ID, REQUEST_ID, LEADER_ID));
    verify(joinRequestRepository, never()).save(any());
  }

  @Test
  void listPendingRequests_clanNotFound_throwsClanNotFoundException() {
    when(clanRepository.findById(CLAN_ID)).thenReturn(Optional.empty());

    assertThrows(
        ClanNotFoundException.class,
        () -> joinRequestService.listPendingRequests(CLAN_ID, LEADER_ID));
  }
}
