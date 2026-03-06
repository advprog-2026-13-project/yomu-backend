package id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload;

import java.util.UUID;
import lombok.Getter;

@Getter
public class AchievementQuizCompletedPayload {
  private final UUID userId;
  private final UUID quizId;
  private final int score;
  private final boolean completed;

  public AchievementQuizCompletedPayload(UUID userId, UUID quizId, int score, boolean completed) {
    this.userId = userId;
    this.quizId = quizId;
    this.score = score;
    this.completed = completed;
  }
}
