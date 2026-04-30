package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IAchievementRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.SpringDataAchievementRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity.AchievementJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AchievementRepositoryAdapter implements IAchievementRepository {

    private final SpringDataAchievementRepository repository;

    public AchievementRepositoryAdapter(SpringDataAchievementRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Achievement> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Achievement> findByAchievementType(AchievementType achievementType) {
        return repository.findByAchievementType(achievementType).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Achievement> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Achievement save(Achievement achievement) {
        AchievementJpaEntity entity = toEntity(achievement);
        AchievementJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private Achievement toDomain(AchievementJpaEntity entity) {
        return new Achievement(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getAchievementType(),
                entity.getMilestone()
        );
    }

    private AchievementJpaEntity toEntity(Achievement domain) {
        return AchievementJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .achievementType(domain.getAchievementType())
                .milestone(domain.getMilestone())
                .build();
    }
}
