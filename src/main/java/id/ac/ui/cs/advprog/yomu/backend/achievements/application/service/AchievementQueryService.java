package id.ac.ui.cs.advprog.yomu.backend.achievements.application.service;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IAchievementRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IDailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IUserAchievementProgressRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IUserDailyMissionProgressRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserDailyMissionProgress;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AchievementQueryService {

  private final IAchievementRepository achievementRepository;
  private final IDailyMissionRepository dailyMissionRepository;
  private final IUserAchievementProgressRepository userAchievementProgressRepository;
  private final IUserDailyMissionProgressRepository userDailyMissionProgressRepository;

  public AchievementQueryService(
      IAchievementRepository achievementRepository,
      IDailyMissionRepository dailyMissionRepository,
      IUserAchievementProgressRepository userAchievementProgressRepository,
      IUserDailyMissionProgressRepository userDailyMissionProgressRepository) {
    this.achievementRepository = achievementRepository;
    this.dailyMissionRepository = dailyMissionRepository;
    this.userAchievementProgressRepository = userAchievementProgressRepository;
    this.userDailyMissionProgressRepository = userDailyMissionProgressRepository;
  }

  public List<Achievement> getAllAchievements() {
    return achievementRepository.findAll();
  }

  public List<DailyMission> getAllDailyMissions() {
    return dailyMissionRepository.findAll();
  }

  public List<UserAchievementProgress> getUserAchievementProgress(UUID userId) {
    return userAchievementProgressRepository.findByUserId(userId);
  }

  public List<UserDailyMissionProgress> getUserDailyMissionProgressForToday(UUID userId) {
    return userDailyMissionProgressRepository.findByUserIdAndDate(userId, LocalDate.now());
  }

  public List<UserAchievementProgress> getCompletedAchievements(UUID userId) {
    return userAchievementProgressRepository.findByUserIdAndIsCompleted(userId, true);
  }

  @Transactional(readOnly = true)
  public long countMembersCompletedDailyMissionOn(List<UUID> userIds, LocalDate date) {
    if (userIds.isEmpty()) return 0L;
    return userDailyMissionProgressRepository.countDistinctCompletedUsersByUserIdInAndDate(
        userIds, date);
  }
}
