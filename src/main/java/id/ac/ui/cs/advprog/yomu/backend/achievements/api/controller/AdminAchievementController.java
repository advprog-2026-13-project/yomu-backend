package id.ac.ui.cs.advprog.yomu.backend.achievements.api.controller;

import id.ac.ui.cs.advprog.yomu.backend.achievements.api.dto.CreateAchievementRequest;
import id.ac.ui.cs.advprog.yomu.backend.achievements.api.dto.CreateDailyMissionRequest;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.service.AchievementAdminService;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/achievements")
public class AdminAchievementController {

  private final AchievementAdminService adminService;

  public AdminAchievementController(AchievementAdminService adminService) {
    this.adminService = adminService;
  }

  @PostMapping
  // Usually you'd uncomment this when security is fully integrated:
  // @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Achievement> createAchievement(
      @Valid @RequestBody CreateAchievementRequest request) {
    Achievement created =
        adminService.createAchievement(
            request.getName(), request.getDescription(), request.getType(), request.getMilestone());
    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAchievement(@PathVariable UUID id) {
    adminService.deleteAchievement(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/daily-missions")
  public ResponseEntity<DailyMission> createDailyMission(
      @Valid @RequestBody CreateDailyMissionRequest request) {
    DailyMission created =
        adminService.createDailyMission(
            request.getName(),
            request.getDescription(),
            request.getTargetType(),
            request.getMilestone());
    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  @DeleteMapping("/daily-missions/{id}")
  public ResponseEntity<Void> deleteDailyMission(@PathVariable UUID id) {
    adminService.deleteDailyMission(id);
    return ResponseEntity.noContent().build();
  }
}
