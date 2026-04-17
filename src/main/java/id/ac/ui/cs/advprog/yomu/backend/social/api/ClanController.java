package id.ac.ui.cs.advprog.yomu.backend.social.api;

import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.yomu.backend.social.application.ClanService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clans")
public class ClanController {

  private final ClanService clanService;

  public ClanController(ClanService clanService) {
    this.clanService = clanService;
  }

  @PostMapping
  public ResponseEntity<ClanResponse> createClan(
      @Valid @RequestBody CreateClanRequest req,
      @AuthenticationPrincipal SecurityUser principal) {
    UUID userId = principal.getUser().getId();
    return ResponseEntity.status(HttpStatus.CREATED).body(clanService.createClan(req.getName(), userId));
  }

  @PostMapping("/{clanId}/join")
  public ResponseEntity<ClanResponse> joinClan(
      @PathVariable UUID clanId, @AuthenticationPrincipal SecurityUser principal) {
    UUID userId = principal.getUser().getId();
    return ResponseEntity.ok(clanService.joinClan(clanId, userId));
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

  @GetMapping("/me")
  public ResponseEntity<ClanResponse> getMyClan(@AuthenticationPrincipal SecurityUser principal) {
    UUID userId = principal.getUser().getId();
    return clanService
        .getMyClan(userId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}