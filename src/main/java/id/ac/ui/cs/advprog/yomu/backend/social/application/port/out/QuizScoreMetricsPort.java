package id.ac.ui.cs.advprog.yomu.backend.social.application.port.out;

import java.util.List;
import java.util.UUID;

public interface QuizScoreMetricsPort {

  double averageScore(List<UUID> userIds);
}
