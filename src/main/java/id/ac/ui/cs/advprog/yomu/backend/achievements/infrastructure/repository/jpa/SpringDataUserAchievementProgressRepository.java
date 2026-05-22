package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa;

import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity.UserAchievementProgressJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataUserAchievementProgressRepository
    extends JpaRepository<UserAchievementProgressJpaEntity, UUID> {
  Optional<UserAchievementProgressJpaEntity> findByUserIdAndAchievementId(
      UUID userId, UUID achievementId);

  List<UserAchievementProgressJpaEntity> findByUserId(UUID userId);

  List<UserAchievementProgressJpaEntity> findByUserIdAndIsCompleted(
      UUID userId, boolean isCompleted);
}
