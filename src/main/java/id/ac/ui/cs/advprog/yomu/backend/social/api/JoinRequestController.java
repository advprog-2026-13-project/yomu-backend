package id.ac.ui.cs.advprog.yomu.backend.social.api;

import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.JoinRequestResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.application.JoinRequestService;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.UserLookupPort;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/social/clans")
public class JoinRequestController {

  private final JoinRequestService joinRequestService;
  private final UserLookupPort userLookup;

  public JoinRequestController(JoinRequestService joinRequestService, UserLookupPort userLookup) {
    this.joinRequestService = joinRequestService;
    this.userLookup = userLookup;
  }

  @PostMapping("/{clanId}/join-requests")
  public ResponseEntity<JoinRequestResponse> submit(
      @PathVariable UUID clanId, @AuthenticationPrincipal SecurityUser principal) {
    UUID userId = principal.getUser().getId();
    JoinRequestResponse response =
        new JoinRequestResponse(joinRequestService.submitJoinRequest(clanId, userId));
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{clanId}/join-requests")
  public ResponseEntity<List<JoinRequestResponse>> listPending(
      @PathVariable UUID clanId, @AuthenticationPrincipal SecurityUser principal) {
    UUID callerId = principal.getUser().getId();
    List<JoinRequestResponse> responses =
        joinRequestService.listPendingRequests(clanId, callerId).stream()
            .map(r -> new JoinRequestResponse(
                r, userLookup.findUsernameById(r.getUserId()).orElse(null)))
            .toList();
    return ResponseEntity.ok(responses);
  }

  @PostMapping("/{clanId}/join-requests/{requestId}/approve")
  public ResponseEntity<Void> approve(
      @PathVariable UUID clanId,
      @PathVariable UUID requestId,
      @AuthenticationPrincipal SecurityUser principal) {
    UUID callerId = principal.getUser().getId();
    joinRequestService.approveRequest(clanId, requestId, callerId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{clanId}/join-requests/{requestId}/reject")
  public ResponseEntity<Void> reject(
      @PathVariable UUID clanId,
      @PathVariable UUID requestId,
      @AuthenticationPrincipal SecurityUser principal) {
    UUID callerId = principal.getUser().getId();
    joinRequestService.rejectRequest(clanId, requestId, callerId);
    return ResponseEntity.ok().build();
  }
}
