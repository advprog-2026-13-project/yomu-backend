package id.ac.ui.cs.advprog.yomu.backend.social.application.port.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface QuizScoreMetricsPort {

  // Returns [0.0–1.0] average accuracy; only attempts completed on or after `since` are counted.
  double averageScore(List<UUID> userIds, LocalDateTime since);
}
