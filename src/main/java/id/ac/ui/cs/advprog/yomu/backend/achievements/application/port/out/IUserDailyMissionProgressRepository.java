package id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out;

import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserDailyMissionProgress;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserDailyMissionProgressRepository {
  Optional<UserDailyMissionProgress> findByUserIdAndMissionIdAndDate(
      UUID userId, UUID missionId, LocalDate date);

  List<UserDailyMissionProgress> findByUserIdAndDate(UUID userId, LocalDate date);

  UserDailyMissionProgress save(UserDailyMissionProgress progress);

  void deleteByDate(LocalDate date);
}
