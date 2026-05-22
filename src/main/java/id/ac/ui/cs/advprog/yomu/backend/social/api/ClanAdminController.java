package id.ac.ui.cs.advprog.yomu.backend.social.api;

import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.JoinRequestResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.application.ClanService;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.JoinRequestRepositoryPort;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/social")
@PreAuthorize("hasRole('ADMIN')")
public class ClanAdminController {

  private final ClanRepositoryPort clanRepository;
  private final ClanMemberRepositoryPort clanMemberRepository;
  private final JoinRequestRepositoryPort joinRequestRepository;
  private final ClanService clanService;

  public ClanAdminController(
      ClanRepositoryPort clanRepository,
      ClanMemberRepositoryPort clanMemberRepository,
      JoinRequestRepositoryPort joinRequestRepository,
      ClanService clanService) {
    this.clanRepository = clanRepository;
    this.clanMemberRepository = clanMemberRepository;
    this.joinRequestRepository = joinRequestRepository;
    this.clanService = clanService;
  }

  @GetMapping("/clans")
  public ResponseEntity<List<ClanResponse>> listClans() {
    var clans =
        clanRepository.findAll().stream()
            .map(c -> new ClanResponse(c, clanMemberRepository.countByClanId(c.getId())))
            .toList();
    return ResponseEntity.ok(clans);
  }

  @DeleteMapping("/clans/{clanId}")
  public ResponseEntity<Void> deleteClan(@PathVariable UUID clanId) {
    clanService.adminDeleteClan(clanId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/clans/{clanId}/members")
  public ResponseEntity<List<ClanMemberResponse>> listMembers(@PathVariable UUID clanId) {
    var members =
        clanMemberRepository.findByClanId(clanId).stream()
            .map(
                m ->
                    new ClanMemberResponse(
                        m.getId(), m.getUserId(), m.getRole().name(), m.getJoinedAt()))
            .toList();
    return ResponseEntity.ok(members);
  }

  @DeleteMapping("/clans/{clanId}/members/{userId}")
  public ResponseEntity<Void> removeMember(@PathVariable UUID clanId, @PathVariable UUID userId) {
    clanService.adminRemoveMember(clanId, userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/join-requests")
  public ResponseEntity<List<JoinRequestResponse>> listJoinRequests() {
    var requests = joinRequestRepository.findAll().stream().map(JoinRequestResponse::new).toList();
    return ResponseEntity.ok(requests);
  }

  record ClanMemberResponse(UUID id, UUID userId, String role, java.time.Instant joinedAt) {}
}
