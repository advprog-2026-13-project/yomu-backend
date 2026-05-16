package id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out;

import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import java.util.UUID;

/**
 * Port for notifying when a user completes an achievement or daily mission. Implementations can
 * range from simple logging to WebSocket push notifications.
 */
public interface IAchievementNotifier {

  /**
   * Called when a user has just unlocked (completed) an achievement.
   *
   * @param userId the user who unlocked the achievement
   * @param achievement the achievement that was unlocked
   */
  void notifyAchievementUnlocked(UUID userId, Achievement achievement);

  /**
   * Called when a user has just completed a daily mission.
   *
   * @param userId the user who completed the mission
   * @param mission the daily mission that was completed
   */
  void notifyDailyMissionCompleted(UUID userId, DailyMission mission);
}
