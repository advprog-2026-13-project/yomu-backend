package id.ac.ui.cs.advprog.yomu.backend.social.api;

import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.application.LeaderboardService;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

  private final LeaderboardService leaderboardService;

  public LeaderboardController(LeaderboardService leaderboardService) {
    this.leaderboardService = leaderboardService;
  }

  @GetMapping
  public ResponseEntity<List<LeaderboardEntryResponse>> getLeaderboard(
      @RequestParam(defaultValue = "BRONZE") Tier tier) {
    return ResponseEntity.ok(leaderboardService.getLeaderboard(tier));
  }
}