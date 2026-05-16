package id.ac.ui.cs.advprog.yomu.backend.achievements.application.service;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IAchievementRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IDailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AchievementAdminService {

  private final IAchievementRepository achievementRepository;
  private final IDailyMissionRepository dailyMissionRepository;

  public AchievementAdminService(
      IAchievementRepository achievementRepository,
      IDailyMissionRepository dailyMissionRepository) {
    this.achievementRepository = achievementRepository;
    this.dailyMissionRepository = dailyMissionRepository;
  }

  public Achievement createAchievement(
      String name, String description, AchievementType type, int milestone) {
    Achievement achievement = new Achievement(null, name, description, type, milestone);
    return achievementRepository.save(achievement);
  }

  public Achievement updateAchievement(
      UUID id, String name, String description, Integer milestone) {
    Achievement existing =
        achievementRepository
            .findById(id)
            .orElseThrow(
                () -> new NoSuchElementException("Achievement not found with id: " + id));
    Achievement updated = existing.update(name, description, milestone);
    return achievementRepository.save(updated);
  }

  public void deleteAchievement(UUID id) {
    achievementRepository.deleteById(id);
  }

  public DailyMission createDailyMission(
      String name, String description, AchievementType targetType, int milestone) {
    DailyMission mission = new DailyMission(null, name, description, targetType, milestone);
    return dailyMissionRepository.save(mission);
  }

  public DailyMission updateDailyMission(
      UUID id, String name, String description, Integer milestone) {
    DailyMission existing =
        dailyMissionRepository
            .findById(id)
            .orElseThrow(
                () -> new NoSuchElementException("Daily mission not found with id: " + id));
    DailyMission updated = existing.update(name, description, milestone);
    return dailyMissionRepository.save(updated);
  }

  public void deleteDailyMission(UUID id) {
    dailyMissionRepository.deleteById(id);
  }
}
