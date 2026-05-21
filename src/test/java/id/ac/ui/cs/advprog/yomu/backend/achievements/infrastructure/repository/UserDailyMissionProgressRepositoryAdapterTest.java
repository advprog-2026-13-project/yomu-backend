package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserDailyMissionProgress;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.SpringDataUserDailyMissionProgressRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity.UserDailyMissionProgressJpaEntity;
import java.time.Instant;
import java.time.LocalDate;
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
class UserDailyMissionProgressRepositoryAdapterTest {

  @Mock private SpringDataUserDailyMissionProgressRepository springDataRepository;

  private UserDailyMissionProgressRepositoryAdapter adapter;

  private UUID progressId;
  private UUID userId;
  private UUID missionId;
  private LocalDate date;
  private Instant completedAt;
  private UserDailyMissionProgressJpaEntity entity;

  @BeforeEach
  void setUp() {
    adapter = new UserDailyMissionProgressRepositoryAdapter(springDataRepository);
    progressId = UUID.randomUUID();
    userId = UUID.randomUUID();
    missionId = UUID.randomUUID();
    date = LocalDate.of(2026, 5, 22);
    completedAt = Instant.now();
    entity =
        UserDailyMissionProgressJpaEntity.builder()
            .id(progressId)
            .userId(userId)
            .missionId(missionId)
            .date(date)
            .currentProgress(2)
            .isCompleted(true)
            .completedAt(completedAt)
            .build();
  }

  @Test
  void findByUserIdAndMissionIdAndDate_WhenExists_ReturnsDomainProgress() {
    when(springDataRepository.findByUserIdAndMissionIdAndDate(userId, missionId, date))
        .thenReturn(Optional.of(entity));

    Optional<UserDailyMissionProgress> result =
        adapter.findByUserIdAndMissionIdAndDate(userId, missionId, date);

    assertThat(result).isPresent();
    UserDailyMissionProgress progress = result.get();
    assertThat(progress.getId()).isEqualTo(progressId);
    assertThat(progress.getUserId()).isEqualTo(userId);
    assertThat(progress.getMissionId()).isEqualTo(missionId);
    assertThat(progress.getDate()).isEqualTo(date);
    assertThat(progress.getCurrentProgress()).isEqualTo(2);
    assertThat(progress.isCompleted()).isTrue();
    assertThat(progress.getCompletedAt()).isEqualTo(completedAt);
  }

  @Test
  void findByUserIdAndMissionIdAndDate_WhenNotExists_ReturnsEmpty() {
    when(springDataRepository.findByUserIdAndMissionIdAndDate(userId, missionId, date))
        .thenReturn(Optional.empty());

    Optional<UserDailyMissionProgress> result =
        adapter.findByUserIdAndMissionIdAndDate(userId, missionId, date);

    assertThat(result).isEmpty();
  }

  @Test
  void findByUserIdAndDate_ReturnsMappedList() {
    when(springDataRepository.findByUserIdAndDate(userId, date)).thenReturn(List.of(entity));

    List<UserDailyMissionProgress> result = adapter.findByUserIdAndDate(userId, date);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getUserId()).isEqualTo(userId);
    assertThat(result.get(0).getDate()).isEqualTo(date);
  }

  @Test
  void findByUserIdAndDate_WhenEmpty_ReturnsEmptyList() {
    when(springDataRepository.findByUserIdAndDate(userId, date)).thenReturn(List.of());

    List<UserDailyMissionProgress> result = adapter.findByUserIdAndDate(userId, date);

    assertThat(result).isEmpty();
  }

  @Test
  void save_ConvertsToEntityAndBack() {
    UserDailyMissionProgress domain =
        new UserDailyMissionProgress(progressId, userId, missionId, date, 2, true, completedAt);

    when(springDataRepository.save(any(UserDailyMissionProgressJpaEntity.class)))
        .thenReturn(entity);

    UserDailyMissionProgress result = adapter.save(domain);

    assertThat(result.getId()).isEqualTo(progressId);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getMissionId()).isEqualTo(missionId);

    ArgumentCaptor<UserDailyMissionProgressJpaEntity> captor =
        ArgumentCaptor.forClass(UserDailyMissionProgressJpaEntity.class);
    verify(springDataRepository).save(captor.capture());
    UserDailyMissionProgressJpaEntity savedEntity = captor.getValue();
    assertThat(savedEntity.getId()).isEqualTo(progressId);
    assertThat(savedEntity.getUserId()).isEqualTo(userId);
    assertThat(savedEntity.getMissionId()).isEqualTo(missionId);
    assertThat(savedEntity.getDate()).isEqualTo(date);
    assertThat(savedEntity.getCurrentProgress()).isEqualTo(2);
    assertThat(savedEntity.isCompleted()).isTrue();
    assertThat(savedEntity.getCompletedAt()).isEqualTo(completedAt);
  }

  @Test
  void deleteByDate_DelegatesToRepository() {
    adapter.deleteByDate(date);

    verify(springDataRepository, times(1)).deleteByDate(date);
  }

  @Test
  void deleteByUserIdAndDate_DelegatesToRepository() {
    adapter.deleteByUserIdAndDate(userId, date);

    verify(springDataRepository, times(1)).deleteByUserIdAndDate(userId, date);
  }
}
