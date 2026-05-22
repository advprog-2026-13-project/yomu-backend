package id.ac.ui.cs.advprog.yomu.backend.reading.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.reading.application.QuizStatsQueryService;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.QuizScoreMetricsPort;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class QuizScoreMetricsAdapter implements QuizScoreMetricsPort {

  private final QuizStatsQueryService quizStatsQueryService;

  public QuizScoreMetricsAdapter(QuizStatsQueryService quizStatsQueryService) {
    this.quizStatsQueryService = quizStatsQueryService;
  }

  @Override
  public double averageScore(List<UUID> userIds) {
    return quizStatsQueryService.averageScore(userIds);
  }
}
