package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IDailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.SpringDataDailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity.DailyMissionJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class DailyMissionRepositoryAdapter implements IDailyMissionRepository {

  private final SpringDataDailyMissionRepository repository;

  public DailyMissionRepositoryAdapter(SpringDataDailyMissionRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<DailyMission> findById(UUID id) {
    return repository.findById(id).map(this::toDomain);
  }

  @Override
  public List<DailyMission> findByTargetType(AchievementType targetType) {
    return repository.findByTargetType(targetType).stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<DailyMission> findAll() {
    return repository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
  }

  @Override
  public DailyMission save(DailyMission mission) {
    DailyMissionJpaEntity entity = toEntity(mission);
    DailyMissionJpaEntity saved = repository.save(entity);
    return toDomain(saved);
  }

  @Override
  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  private DailyMission toDomain(DailyMissionJpaEntity entity) {
    return new DailyMission(
        entity.getId(),
        entity.getName(),
        entity.getDescription(),
        entity.getTargetType(),
        entity.getMilestone());
  }

  private DailyMissionJpaEntity toEntity(DailyMission domain) {
    return DailyMissionJpaEntity.builder()
        .id(domain.getId())
        .name(domain.getName())
        .description(domain.getDescription())
        .targetType(domain.getTargetType())
        .milestone(domain.getMilestone())
        .build();
  }
}
