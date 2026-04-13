package id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out;

import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDailyMissionRepository {
  Optional<DailyMission> findById(UUID id);

  List<DailyMission> findAll();

  List<DailyMission> findByTargetType(AchievementType targetType);

  DailyMission save(DailyMission mission);

  void deleteById(UUID id);
}
