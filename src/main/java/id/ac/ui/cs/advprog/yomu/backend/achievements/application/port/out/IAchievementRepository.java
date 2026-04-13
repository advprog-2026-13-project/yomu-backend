package id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out;

import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAchievementRepository {
  Optional<Achievement> findById(UUID id);

  List<Achievement> findAll();

  List<Achievement> findByAchievementType(AchievementType type);

  Achievement save(Achievement achievement);

  void deleteById(UUID id);
}
