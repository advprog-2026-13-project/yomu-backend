package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity.AchievementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataAchievementRepository extends JpaRepository<AchievementJpaEntity, UUID> {
    List<AchievementJpaEntity> findByAchievementType(AchievementType achievementType);
}
