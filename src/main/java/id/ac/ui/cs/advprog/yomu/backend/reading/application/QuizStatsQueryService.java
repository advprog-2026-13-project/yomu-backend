package id.ac.ui.cs.advprog.yomu.backend.reading.application;

import id.ac.ui.cs.advprog.yomu.backend.reading.domain.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.backend.reading.infrastructure.QuizAttemptRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QuizStatsQueryService {

  private final QuizAttemptRepository quizAttemptRepository;

  public QuizStatsQueryService(QuizAttemptRepository quizAttemptRepository) {
    this.quizAttemptRepository = quizAttemptRepository;
  }

  /**
   * Returns average quiz score for the given users as a value in [0.0, 1.0], counting only attempts
   * completed at or after {@code since}. Default 1.0 when no attempts exist in the window — clan
   * with no quiz history is not penalised (LowAccuracyPenalty only triggers when result is below
   * 0.5). Attempts with a null completion time are always included (treated as in-window).
   */
  public double averageScore(List<UUID> userIds, LocalDateTime since) {
    if (userIds.isEmpty()) return 1.0;
    List<QuizAttempt> attempts =
        quizAttemptRepository.findByStudentIdIn(userIds).stream()
            .filter(a -> a.getCompletedAt() == null || !a.getCompletedAt().isBefore(since))
            .toList();
    if (attempts.isEmpty()) return 1.0;
    return attempts.stream().mapToInt(QuizAttempt::getScore).average().orElse(100.0) / 100.0;
  }
}
