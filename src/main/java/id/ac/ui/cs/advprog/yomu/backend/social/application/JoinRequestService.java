package id.ac.ui.cs.advprog.yomu.backend.social.application;

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
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JoinRequestService {

  private final ClanRepositoryPort clanRepository;
  private final ClanMemberRepositoryPort clanMemberRepository;
  private final JoinRequestRepositoryPort joinRequestRepository;

  public JoinRequestService(
      ClanRepositoryPort clanRepository,
      ClanMemberRepositoryPort clanMemberRepository,
      JoinRequestRepositoryPort joinRequestRepository) {
    this.clanRepository = clanRepository;
    this.clanMemberRepository = clanMemberRepository;
    this.joinRequestRepository = joinRequestRepository;
  }

  public JoinRequest submitJoinRequest(UUID clanId, UUID userId) {
    clanRepository
        .findById(clanId)
        .orElseThrow(() -> new ClanNotFoundException("Clan not found: " + clanId));
    if (clanMemberRepository.existsByUserId(userId)) {
      throw new AlreadyMemberException("User is already a member of a clan");
    }
    if (joinRequestRepository.existsByClanIdAndUserIdAndStatus(
        clanId, userId, JoinRequestStatus.PENDING)) {
      throw new DuplicateJoinRequestException(
          "A pending join request already exists for this clan");
    }
    return joinRequestRepository.save(JoinRequest.create(clanId, userId));
  }

  @Transactional(readOnly = true)
  public List<JoinRequest> listPendingRequests(UUID clanId, UUID callerId) {
    Clan clan =
        clanRepository
            .findById(clanId)
            .orElseThrow(() -> new ClanNotFoundException("Clan not found: " + clanId));
    if (!clan.getLeaderId().equals(callerId)) {
      throw new NotClanLeaderException("Only the clan leader can view join requests");
    }
    return joinRequestRepository.findByClanIdAndStatus(clanId, JoinRequestStatus.PENDING);
  }

  public void approveRequest(UUID clanId, UUID requestId, UUID callerId) {
    Clan clan =
        clanRepository
            .findById(clanId)
            .orElseThrow(() -> new ClanNotFoundException("Clan not found: " + clanId));
    JoinRequest request =
        joinRequestRepository
            .findById(requestId)
            .orElseThrow(() -> new ClanNotFoundException("Join request not found: " + requestId));
    if (!clan.getLeaderId().equals(callerId)) {
      throw new NotClanLeaderException("Only the clan leader can approve join requests");
    }
    try {
      request.approve();
    } catch (IllegalStateException e) {
      throw new JoinRequestAlreadyResolvedException(e.getMessage());
    }
    if (clanMemberRepository.existsByUserId(request.getUserId())) {
      throw new AlreadyMemberException("User is already a member of a clan");
    }
    clanMemberRepository.save(ClanMember.join(request.getClanId(), request.getUserId()));
    joinRequestRepository.save(request);
  }

  public void rejectRequest(UUID clanId, UUID requestId, UUID callerId) {
    Clan clan =
        clanRepository
            .findById(clanId)
            .orElseThrow(() -> new ClanNotFoundException("Clan not found: " + clanId));
    JoinRequest request =
        joinRequestRepository
            .findById(requestId)
            .orElseThrow(() -> new ClanNotFoundException("Join request not found: " + requestId));
    if (!clan.getLeaderId().equals(callerId)) {
      throw new NotClanLeaderException("Only the clan leader can reject join requests");
    }
    try {
      request.reject();
    } catch (IllegalStateException e) {
      throw new JoinRequestAlreadyResolvedException(e.getMessage());
    }
    joinRequestRepository.save(request);
  }
}
