package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.SpringDataAchievementRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.repository.jpa.entity.AchievementJpaEntity;
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
class AchievementRepositoryAdapterTest {

  @Mock private SpringDataAchievementRepository springDataRepository;

  private AchievementRepositoryAdapter adapter;

  private UUID achievementId;
  private AchievementJpaEntity entity;

  @BeforeEach
  void setUp() {
    adapter = new AchievementRepositoryAdapter(springDataRepository);
    achievementId = UUID.randomUUID();
    entity =
        AchievementJpaEntity.builder()
            .id(achievementId)
            .name("Read 5 Books")
            .description("Complete reading 5 books")
            .achievementType(AchievementType.READING_COMPLETED)
            .milestone(5)
            .build();
  }

  // --- findById ---

  @Test
  void findById_WhenExists_ReturnsDomainAchievement() {
    when(springDataRepository.findById(achievementId)).thenReturn(Optional.of(entity));

    Optional<Achievement> result = adapter.findById(achievementId);

    assertThat(result).isPresent();
    Achievement achievement = result.get();
    assertThat(achievement.getId()).isEqualTo(achievementId);
    assertThat(achievement.getName()).isEqualTo("Read 5 Books");
    assertThat(achievement.getDescription()).isEqualTo("Complete reading 5 books");
    assertThat(achievement.getAchievementType()).isEqualTo(AchievementType.READING_COMPLETED);
    assertThat(achievement.getMilestone()).isEqualTo(5);
  }

  @Test
  void findById_WhenNotExists_ReturnsEmpty() {
    when(springDataRepository.findById(achievementId)).thenReturn(Optional.empty());

    Optional<Achievement> result = adapter.findById(achievementId);

    assertThat(result).isEmpty();
  }

  // --- findByAchievementType ---

  @Test
  void findByAchievementType_ReturnsMappedList() {
    AchievementJpaEntity entity2 =
        AchievementJpaEntity.builder()
            .id(UUID.randomUUID())
            .name("Read 10 Books")
            .description("Complete reading 10 books")
            .achievementType(AchievementType.READING_COMPLETED)
            .milestone(10)
            .build();

    when(springDataRepository.findByAchievementType(AchievementType.READING_COMPLETED))
        .thenReturn(List.of(entity, entity2));

    List<Achievement> result = adapter.findByAchievementType(AchievementType.READING_COMPLETED);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("Read 5 Books");
    assertThat(result.get(1).getName()).isEqualTo("Read 10 Books");
  }

  @Test
  void findByAchievementType_WhenEmpty_ReturnsEmptyList() {
    when(springDataRepository.findByAchievementType(AchievementType.QUIZ_COMPLETED))
        .thenReturn(List.of());

    List<Achievement> result = adapter.findByAchievementType(AchievementType.QUIZ_COMPLETED);

    assertThat(result).isEmpty();
  }

  // --- findAll ---

  @Test
  void findAll_ReturnsMappedList() {
    when(springDataRepository.findAll()).thenReturn(List.of(entity));

    List<Achievement> result = adapter.findAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(achievementId);
    assertThat(result.get(0).getName()).isEqualTo("Read 5 Books");
  }

  @Test
  void findAll_WhenEmpty_ReturnsEmptyList() {
    when(springDataRepository.findAll()).thenReturn(List.of());

    List<Achievement> result = adapter.findAll();

    assertThat(result).isEmpty();
  }

  // --- save ---

  @Test
  void save_ConvertsToEntityAndBack() {
    Achievement domain =
        new Achievement(
            achievementId,
            "Read 5 Books",
            "Complete reading 5 books",
            AchievementType.READING_COMPLETED,
            5);

    when(springDataRepository.save(any(AchievementJpaEntity.class))).thenReturn(entity);

    Achievement result = adapter.save(domain);

    assertThat(result.getId()).isEqualTo(achievementId);
    assertThat(result.getName()).isEqualTo("Read 5 Books");
    assertThat(result.getAchievementType()).isEqualTo(AchievementType.READING_COMPLETED);
    assertThat(result.getMilestone()).isEqualTo(5);

    ArgumentCaptor<AchievementJpaEntity> captor =
        ArgumentCaptor.forClass(AchievementJpaEntity.class);
    verify(springDataRepository).save(captor.capture());
    AchievementJpaEntity savedEntity = captor.getValue();
    assertThat(savedEntity.getId()).isEqualTo(achievementId);
    assertThat(savedEntity.getName()).isEqualTo("Read 5 Books");
    assertThat(savedEntity.getDescription()).isEqualTo("Complete reading 5 books");
    assertThat(savedEntity.getAchievementType()).isEqualTo(AchievementType.READING_COMPLETED);
    assertThat(savedEntity.getMilestone()).isEqualTo(5);
  }

  @Test
  void save_WithNewAchievement_DelegatesToRepository() {
    Achievement domain =
        new Achievement(
            null, "Quiz Master", "Complete 10 quizzes", AchievementType.QUIZ_COMPLETED, 10);

    AchievementJpaEntity savedEntity =
        AchievementJpaEntity.builder()
            .id(domain.getId())
            .name("Quiz Master")
            .description("Complete 10 quizzes")
            .achievementType(AchievementType.QUIZ_COMPLETED)
            .milestone(10)
            .build();

    when(springDataRepository.save(any(AchievementJpaEntity.class))).thenReturn(savedEntity);

    Achievement result = adapter.save(domain);

    assertThat(result.getName()).isEqualTo("Quiz Master");
    assertThat(result.getAchievementType()).isEqualTo(AchievementType.QUIZ_COMPLETED);
    verify(springDataRepository, times(1)).save(any(AchievementJpaEntity.class));
  }

  // --- deleteById ---

  @Test
  void deleteById_DelegatesToRepository() {
    adapter.deleteById(achievementId);

    verify(springDataRepository, times(1)).deleteById(achievementId);
  }
}
