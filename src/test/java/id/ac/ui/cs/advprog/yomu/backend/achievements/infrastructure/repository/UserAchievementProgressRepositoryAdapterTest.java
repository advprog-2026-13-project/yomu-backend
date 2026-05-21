package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.SpringDataUserAchievementProgressRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity.UserAchievementProgressJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAchievementProgressRepositoryAdapterTest {

  @Mock private SpringDataUserAchievementProgressRepository springDataRepository;

  private UserAchievementProgressRepositoryAdapter adapter;

  private UUID progressId;
  private UUID userId;
  private UUID achievementId;
  private Instant completedAt;
  private UserAchievementProgressJpaEntity entity;

  @BeforeEach
  void setUp() {
    adapter = new UserAchievementProgressRepositoryAdapter(springDataRepository);
    progressId = UUID.randomUUID();
    userId = UUID.randomUUID();
    achievementId = UUID.randomUUID();
    completedAt = Instant.now();
    entity =
        UserAchievementProgressJpaEntity.builder()
            .id(progressId)
            .userId(userId)
            .achievementId(achievementId)
            .currentProgress(3)
            .isCompleted(true)
            .completedAt(completedAt)
            .isDisplayedOnProfile(false)
            .build();
  }

  @Test
  void findByUserIdAndAchievementId_WhenExists_ReturnsDomainProgress() {
    when(springDataRepository.findByUserIdAndAchievementId(userId, achievementId))
        .thenReturn(Optional.of(entity));

    Optional<UserAchievementProgress> result =
        adapter.findByUserIdAndAchievementId(userId, achievementId);

    assertThat(result).isPresent();
    UserAchievementProgress progress = result.get();
    assertThat(progress.getId()).isEqualTo(progressId);
    assertThat(progress.getUserId()).isEqualTo(userId);
    assertThat(progress.getAchievementId()).isEqualTo(achievementId);
    assertThat(progress.getCurrentProgress()).isEqualTo(3);
    assertThat(progress.isCompleted()).isTrue();
    assertThat(progress.getCompletedAt()).isEqualTo(completedAt);
    assertThat(progress.isDisplayedOnProfile()).isFalse();
  }

  @Test
  void findByUserIdAndAchievementId_WhenNotExists_ReturnsEmpty() {
    when(springDataRepository.findByUserIdAndAchievementId(userId, achievementId))
        .thenReturn(Optional.empty());

    Optional<UserAchievementProgress> result =
        adapter.findByUserIdAndAchievementId(userId, achievementId);

    assertThat(result).isEmpty();
  }

  @Test
  void findByUserId_ReturnsMappedList() {
    when(springDataRepository.findByUserId(userId)).thenReturn(List.of(entity));

    List<UserAchievementProgress> result = adapter.findByUserId(userId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getUserId()).isEqualTo(userId);
    assertThat(result.get(0).getCurrentProgress()).isEqualTo(3);
  }

  @Test
  void findByUserId_WhenEmpty_ReturnsEmptyList() {
    when(springDataRepository.findByUserId(userId)).thenReturn(List.of());

    List<UserAchievementProgress> result = adapter.findByUserId(userId);

    assertThat(result).isEmpty();
  }

  @Test
  void findByUserIdAndIsCompleted_ReturnsMappedList() {
    when(springDataRepository.findByUserIdAndIsCompleted(userId, true))
        .thenReturn(List.of(entity));

    List<UserAchievementProgress> result = adapter.findByUserIdAndIsCompleted(userId, true);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).isCompleted()).isTrue();
  }

  @Test
  void findByUserIdAndIsCompleted_WhenEmpty_ReturnsEmptyList() {
    when(springDataRepository.findByUserIdAndIsCompleted(userId, false))
        .thenReturn(List.of());

    List<UserAchievementProgress> result = adapter.findByUserIdAndIsCompleted(userId, false);

    assertThat(result).isEmpty();
  }

  @Test
  void save_ConvertsToEntityAndBack() {
    UserAchievementProgress domain =
        new UserAchievementProgress(
            progressId, userId, achievementId, 3, true, completedAt, false);

    when(springDataRepository.save(any(UserAchievementProgressJpaEntity.class)))
        .thenReturn(entity);

    UserAchievementProgress result = adapter.save(domain);

    assertThat(result.getId()).isEqualTo(progressId);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getAchievementId()).isEqualTo(achievementId);

    ArgumentCaptor<UserAchievementProgressJpaEntity> captor =
        ArgumentCaptor.forClass(UserAchievementProgressJpaEntity.class);
    verify(springDataRepository).save(captor.capture());
    UserAchievementProgressJpaEntity savedEntity = captor.getValue();
    assertThat(savedEntity.getId()).isEqualTo(progressId);
    assertThat(savedEntity.getUserId()).isEqualTo(userId);
    assertThat(savedEntity.getAchievementId()).isEqualTo(achievementId);
    assertThat(savedEntity.getCurrentProgress()).isEqualTo(3);
    assertThat(savedEntity.isCompleted()).isTrue();
    assertThat(savedEntity.getCompletedAt()).isEqualTo(completedAt);
    assertThat(savedEntity.isDisplayedOnProfile()).isFalse();
  }
}
