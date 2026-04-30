package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa;

import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity.UserDailyMissionProgressJpaEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataUserDailyMissionProgressRepository
    extends JpaRepository<UserDailyMissionProgressJpaEntity, UUID> {
  Optional<UserDailyMissionProgressJpaEntity> findByUserIdAndMissionIdAndDate(
      UUID userId, UUID missionId, LocalDate date);

  List<UserDailyMissionProgressJpaEntity> findByUserIdAndDate(UUID userId, LocalDate date);

  @Modifying
  @Query("DELETE FROM UserDailyMissionProgressJpaEntity p WHERE p.date = :date")
  void deleteByDate(LocalDate date);
}
