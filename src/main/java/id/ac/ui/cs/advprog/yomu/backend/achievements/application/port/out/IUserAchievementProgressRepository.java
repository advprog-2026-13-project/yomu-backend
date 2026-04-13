package id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out;

import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserAchievementProgress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserAchievementProgressRepository {
  Optional<UserAchievementProgress> findByUserIdAndAchievementId(UUID userId, UUID achievementId);

  List<UserAchievementProgress> findByUserId(UUID userId);

  List<UserAchievementProgress> findByUserIdAndIsCompleted(UUID userId, boolean isCompleted);

  UserAchievementProgress save(UserAchievementProgress progress);
}
