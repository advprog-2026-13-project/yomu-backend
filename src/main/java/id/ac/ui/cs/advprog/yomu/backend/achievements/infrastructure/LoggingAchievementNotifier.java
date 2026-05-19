package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IAchievementNotifier;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Logging-based implementation of {@link IAchievementNotifier}.
 *
 * <p>This is a simple starting point that logs achievement/mission completions. It can later be
 * replaced or decorated with WebSocket, push notification, or in-app notification adapters without
 * changing any service code.
 */
@Component
public class LoggingAchievementNotifier implements IAchievementNotifier {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAchievementNotifier.class);

  @Override
  public void notifyAchievementUnlocked(UUID userId, Achievement achievement) {
    logger.info(
        "🏆 Achievement unlocked! userId={}, achievement='{}' (id={})",
        userId,
        achievement.getName(),
        achievement.getId());
  }

  @Override
  public void notifyDailyMissionCompleted(UUID userId, DailyMission mission) {
    logger.info(
        "✅ Daily mission completed! userId={}, mission='{}' (id={})",
        userId,
        mission.getName(),
        mission.getId());
  }
}
