package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IUserAchievementProgressRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.SpringDataUserAchievementProgressRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity.UserAchievementProgressJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserAchievementProgressRepositoryAdapter implements IUserAchievementProgressRepository {

    private final SpringDataUserAchievementProgressRepository repository;

    public UserAchievementProgressRepositoryAdapter(SpringDataUserAchievementProgressRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<UserAchievementProgress> findByUserIdAndAchievementId(UUID userId, UUID achievementId) {
        return repository.findByUserIdAndAchievementId(userId, achievementId).map(this::toDomain);
    }

    @Override
    public List<UserAchievementProgress> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserAchievementProgress> findByUserIdAndIsCompleted(UUID userId, boolean isCompleted) {
        return repository.findByUserIdAndIsCompleted(userId, isCompleted).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public UserAchievementProgress save(UserAchievementProgress progress) {
        UserAchievementProgressJpaEntity entity = toEntity(progress);
        UserAchievementProgressJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    private UserAchievementProgress toDomain(UserAchievementProgressJpaEntity entity) {
        return new UserAchievementProgress(
                entity.getId(),
                entity.getUserId(),
                entity.getAchievementId(),
                entity.getCurrentProgress(),
                entity.isCompleted(),
                entity.getCompletedAt(),
                entity.isDisplayedOnProfile()
        );
    }

    private UserAchievementProgressJpaEntity toEntity(UserAchievementProgress domain) {
        return UserAchievementProgressJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .achievementId(domain.getAchievementId())
                .currentProgress(domain.getCurrentProgress())
                .isCompleted(domain.isCompleted())
                .completedAt(domain.getCompletedAt())
                .isDisplayedOnProfile(domain.isDisplayedOnProfile())
                .build();
    }
}
