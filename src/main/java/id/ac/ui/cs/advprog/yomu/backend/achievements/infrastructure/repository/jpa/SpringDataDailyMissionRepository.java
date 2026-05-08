package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity.DailyMissionJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataDailyMissionRepository
    extends JpaRepository<DailyMissionJpaEntity, UUID> {
  List<DailyMissionJpaEntity> findByTargetType(AchievementType targetType);
}
