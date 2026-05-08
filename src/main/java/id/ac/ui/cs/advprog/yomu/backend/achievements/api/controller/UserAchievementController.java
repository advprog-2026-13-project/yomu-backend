package id.ac.ui.cs.advprog.yomu.backend.achievements.api.controller;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.service.AchievementQueryService;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserDailyMissionProgress;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/achievements")
public class UserAchievementController {

  private final AchievementQueryService queryService;

  public UserAchievementController(AchievementQueryService queryService) {
    this.queryService = queryService;
  }

  @GetMapping
  public ResponseEntity<List<Achievement>> getAllAchievements() {
    return ResponseEntity.ok(queryService.getAllAchievements());
  }

  @GetMapping("/daily-missions")
  public ResponseEntity<List<DailyMission>> getAllDailyMissions() {
    return ResponseEntity.ok(queryService.getAllDailyMissions());
  }

  @GetMapping("/users/{userId}/progress")
  public ResponseEntity<List<UserAchievementProgress>> getUserAchievementProgress(
      @PathVariable UUID userId) {
    return ResponseEntity.ok(queryService.getUserAchievementProgress(userId));
  }

  @GetMapping("/users/{userId}/daily-progress")
  public ResponseEntity<List<UserDailyMissionProgress>> getUserDailyMissionProgress(
      @PathVariable UUID userId) {
    return ResponseEntity.ok(queryService.getUserDailyMissionProgressForToday(userId));
  }

  @GetMapping("/users/{userId}/completed")
  public ResponseEntity<List<UserAchievementProgress>> getCompletedAchievements(
      @PathVariable UUID userId) {
    return ResponseEntity.ok(queryService.getCompletedAchievements(userId));
  }
}
