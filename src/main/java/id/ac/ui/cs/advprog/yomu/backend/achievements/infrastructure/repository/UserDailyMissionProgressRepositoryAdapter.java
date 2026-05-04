package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IUserDailyMissionProgressRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserDailyMissionProgress;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.SpringDataUserDailyMissionProgressRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity.UserDailyMissionProgressJpaEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserDailyMissionProgressRepositoryAdapter
    implements IUserDailyMissionProgressRepository {

  private final SpringDataUserDailyMissionProgressRepository repository;

  public UserDailyMissionProgressRepositoryAdapter(
      SpringDataUserDailyMissionProgressRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<UserDailyMissionProgress> findByUserIdAndMissionIdAndDate(
      UUID userId, UUID missionId, LocalDate date) {
    return repository.findByUserIdAndMissionIdAndDate(userId, missionId, date).map(this::toDomain);
  }

  @Override
  public List<UserDailyMissionProgress> findByUserIdAndDate(UUID userId, LocalDate date) {
    return repository.findByUserIdAndDate(userId, date).stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public UserDailyMissionProgress save(UserDailyMissionProgress progress) {
    UserDailyMissionProgressJpaEntity entity = toEntity(progress);
    UserDailyMissionProgressJpaEntity saved = repository.save(entity);
    return toDomain(saved);
  }

  @Override
  public void deleteByDate(LocalDate date) {
    repository.deleteByDate(date);
  }

  private UserDailyMissionProgress toDomain(UserDailyMissionProgressJpaEntity entity) {
    return new UserDailyMissionProgress(
        entity.getId(),
        entity.getUserId(),
        entity.getMissionId(),
        entity.getDate(),
        entity.getCurrentProgress(),
        entity.isCompleted(),
        entity.getCompletedAt());
  }

  private UserDailyMissionProgressJpaEntity toEntity(UserDailyMissionProgress domain) {
    return UserDailyMissionProgressJpaEntity.builder()
        .id(domain.getId())
        .userId(domain.getUserId())
        .missionId(domain.getMissionId())
        .date(domain.getDate())
        .currentProgress(domain.getCurrentProgress())
        .isCompleted(domain.isCompleted())
        .completedAt(domain.getCompletedAt())
        .build();
  }
}
