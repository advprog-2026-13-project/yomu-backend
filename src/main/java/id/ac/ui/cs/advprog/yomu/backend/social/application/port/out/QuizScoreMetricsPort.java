package id.ac.ui.cs.advprog.yomu.backend.social.application.port.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface QuizScoreMetricsPort {

  /**
   * Rata-rata akurasi kuis (0.0–1.0) untuk user yang percobaannya selesai pada {@code since} atau
   * sesudahnya. Memungkinkan perhitungan di-scope per-season (reset saat end-season).
   */
  double averageScore(List<UUID> userIds, LocalDateTime since);
}
