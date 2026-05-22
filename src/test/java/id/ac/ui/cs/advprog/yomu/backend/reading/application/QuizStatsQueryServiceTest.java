package id.ac.ui.cs.advprog.yomu.backend.reading.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.yomu.backend.reading.domain.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.backend.reading.infrastructure.QuizAttemptRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuizStatsQueryServiceTest {

  @Mock private QuizAttemptRepository quizAttemptRepository;

  private QuizStatsQueryService quizStatsQueryService;

  @BeforeEach
  void setUp() {
    quizStatsQueryService = new QuizStatsQueryService(quizAttemptRepository);
  }

  @Test
  void averageScore_returnsAverageOfAllAttemptsAsZeroToOne() {
    UUID u1 = UUID.randomUUID();
    UUID u2 = UUID.randomUUID();
    List<UUID> userIds = List.of(u1, u2);

    QuizAttempt a1 = new QuizAttempt();
    a1.setScore(80);
    QuizAttempt a2 = new QuizAttempt();
    a2.setScore(60);
    QuizAttempt a3 = new QuizAttempt();
    a3.setScore(100);

    when(quizAttemptRepository.findByStudentIdIn(userIds)).thenReturn(List.of(a1, a2, a3));

    assertEquals(0.8, quizStatsQueryService.averageScore(userIds), 0.001);
  }

  @Test
  void averageScore_returnsOnePointZeroWhenUserIdsEmpty() {
    assertEquals(1.0, quizStatsQueryService.averageScore(List.of()), 0.001);
  }

  @Test
  void averageScore_returnsOnePointZeroWhenNoAttemptsFound() {
    List<UUID> userIds = List.of(UUID.randomUUID());
    when(quizAttemptRepository.findByStudentIdIn(userIds)).thenReturn(List.of());

    assertEquals(1.0, quizStatsQueryService.averageScore(userIds), 0.001);
  }

  @Test
  void averageScore_returnsZeroWhenAllScoresAreZero() {
    List<UUID> userIds = List.of(UUID.randomUUID());

    QuizAttempt a1 = new QuizAttempt();
    a1.setScore(0);
    QuizAttempt a2 = new QuizAttempt();
    a2.setScore(0);

    when(quizAttemptRepository.findByStudentIdIn(userIds)).thenReturn(List.of(a1, a2));

    assertEquals(0.0, quizStatsQueryService.averageScore(userIds), 0.001);
  }
}
