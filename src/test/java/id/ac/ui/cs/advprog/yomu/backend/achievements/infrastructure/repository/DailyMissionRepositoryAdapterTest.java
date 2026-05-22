package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.SpringDataDailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity.DailyMissionJpaEntity;
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
class DailyMissionRepositoryAdapterTest {

  @Mock private SpringDataDailyMissionRepository springDataRepository;

  private DailyMissionRepositoryAdapter adapter;

  private UUID missionId;
  private DailyMissionJpaEntity entity;

  @BeforeEach
  void setUp() {
    adapter = new DailyMissionRepositoryAdapter(springDataRepository);
    missionId = UUID.randomUUID();
    entity =
        DailyMissionJpaEntity.builder()
            .id(missionId)
            .name("Read 1 Book Today")
            .description("Complete reading 1 book today")
            .targetType(AchievementType.READING_COMPLETED)
            .milestone(1)
            .build();
  }

  @Test
  void findById_WhenExists_ReturnsDomainMission() {
    when(springDataRepository.findById(missionId)).thenReturn(Optional.of(entity));

    Optional<DailyMission> result = adapter.findById(missionId);

    assertThat(result).isPresent();
    DailyMission mission = result.get();
    assertThat(mission.getId()).isEqualTo(missionId);
    assertThat(mission.getName()).isEqualTo("Read 1 Book Today");
    assertThat(mission.getDescription()).isEqualTo("Complete reading 1 book today");
    assertThat(mission.getTargetType()).isEqualTo(AchievementType.READING_COMPLETED);
    assertThat(mission.getMilestone()).isEqualTo(1);
  }

  @Test
  void findById_WhenNotExists_ReturnsEmpty() {
    when(springDataRepository.findById(missionId)).thenReturn(Optional.empty());

    Optional<DailyMission> result = adapter.findById(missionId);

    assertThat(result).isEmpty();
  }

  @Test
  void findByTargetType_ReturnsMappedList() {
    when(springDataRepository.findByTargetType(AchievementType.READING_COMPLETED))
        .thenReturn(List.of(entity));

    List<DailyMission> result = adapter.findByTargetType(AchievementType.READING_COMPLETED);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("Read 1 Book Today");
  }

  @Test
  void findByTargetType_WhenEmpty_ReturnsEmptyList() {
    when(springDataRepository.findByTargetType(AchievementType.QUIZ_COMPLETED))
        .thenReturn(List.of());

    List<DailyMission> result = adapter.findByTargetType(AchievementType.QUIZ_COMPLETED);

    assertThat(result).isEmpty();
  }

  @Test
  void findAll_ReturnsMappedList() {
    when(springDataRepository.findAll()).thenReturn(List.of(entity));

    List<DailyMission> result = adapter.findAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(missionId);
  }

  @Test
  void findAll_WhenEmpty_ReturnsEmptyList() {
    when(springDataRepository.findAll()).thenReturn(List.of());

    List<DailyMission> result = adapter.findAll();

    assertThat(result).isEmpty();
  }

  @Test
  void save_ConvertsToEntityAndBack() {
    DailyMission domain =
        new DailyMission(
            missionId,
            "Read 1 Book Today",
            "Complete reading 1 book today",
            AchievementType.READING_COMPLETED,
            1);

    when(springDataRepository.save(any(DailyMissionJpaEntity.class))).thenReturn(entity);

    DailyMission result = adapter.save(domain);

    assertThat(result.getId()).isEqualTo(missionId);
    assertThat(result.getName()).isEqualTo("Read 1 Book Today");

    ArgumentCaptor<DailyMissionJpaEntity> captor =
        ArgumentCaptor.forClass(DailyMissionJpaEntity.class);
    verify(springDataRepository).save(captor.capture());
    DailyMissionJpaEntity savedEntity = captor.getValue();
    assertThat(savedEntity.getId()).isEqualTo(missionId);
    assertThat(savedEntity.getName()).isEqualTo("Read 1 Book Today");
    assertThat(savedEntity.getTargetType()).isEqualTo(AchievementType.READING_COMPLETED);
    assertThat(savedEntity.getMilestone()).isEqualTo(1);
  }

  @Test
  void deleteById_DelegatesToRepository() {
    adapter.deleteById(missionId);

    verify(springDataRepository, times(1)).deleteById(missionId);
  }
}
