package id.ac.ui.cs.advprog.yomu.backend.social.api;

import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.yomu.backend.social.application.ClanService;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.UserLookupPort;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/social/clans")
public class ClanController {

  private final ClanService clanService;
  private final ClanMemberRepositoryPort clanMemberRepository;
  private final UserLookupPort userLookup;

  public ClanController(
      ClanService clanService,
      ClanMemberRepositoryPort clanMemberRepository,
      UserLookupPort userLookup) {
    this.clanService = clanService;
    this.clanMemberRepository = clanMemberRepository;
    this.userLookup = userLookup;
  }

  @PostMapping
  public ResponseEntity<ClanResponse> createClan(
      @Valid @RequestBody CreateClanRequest req, @AuthenticationPrincipal SecurityUser principal) {
    UUID userId = principal.getUser().getId();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(clanService.createClan(req.getName(), userId));
  }

  @DeleteMapping("/leave")
  public ResponseEntity<Void> leaveClan(@AuthenticationPrincipal SecurityUser principal) {
    UUID userId = principal.getUser().getId();
    clanService.leaveClan(userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{clanId}")
  public ResponseEntity<ClanResponse> getClan(@PathVariable UUID clanId) {
    return ResponseEntity.ok(clanService.getClan(clanId));
  }

  @GetMapping("/{clanId}/members")
  public ResponseEntity<List<ClanMemberInfo>> getClanMembers(@PathVariable UUID clanId) {
    clanService.getClan(clanId); // validates clan exists; throws ClanNotFoundException if not
    var members =
        clanMemberRepository.findByClanId(clanId).stream()
            .map(
                m ->
                    new ClanMemberInfo(
                        m.getUserId(),
                        userLookup.findUsernameById(m.getUserId()).orElse(null),
                        m.getRole().name(),
                        m.getJoinedAt()))
            .toList();
    return ResponseEntity.ok(members);
  }

  @DeleteMapping("/{clanId}")
  public ResponseEntity<Void> deleteClan(
      @PathVariable UUID clanId, @AuthenticationPrincipal SecurityUser principal) {
    UUID callerId = principal.getUser().getId();
    clanService.deleteClan(clanId, callerId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me")
  public ResponseEntity<ClanResponse> getMyClan(@AuthenticationPrincipal SecurityUser principal) {
    UUID userId = principal.getUser().getId();
    return clanService
        .getMyClan(userId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  record ClanMemberInfo(UUID userId, String username, String role, Instant joinedAt) {}
}
