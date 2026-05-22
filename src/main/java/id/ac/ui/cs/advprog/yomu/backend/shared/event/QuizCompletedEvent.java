package id.ac.ui.cs.advprog.yomu.backend.shared.event;

import java.util.UUID;
import org.springframework.context.ApplicationEvent;

public class QuizCompletedEvent extends ApplicationEvent {

  private final UUID userId;
  private final UUID readingId;
  private final int correctAnswers;
  private final int totalQuestions;
  private final long scoreEarned;

  public QuizCompletedEvent(
      Object source,
      UUID userId,
      UUID readingId,
      int correctAnswers,
      int totalQuestions,
      long scoreEarned) {
    super(source);
    this.userId = userId;
    this.readingId = readingId;
    this.correctAnswers = correctAnswers;
    this.totalQuestions = totalQuestions;
    this.scoreEarned = scoreEarned;
  }

  public UUID getUserId() {
    return userId;
  }

  public UUID getReadingId() {
    return readingId;
  }

  public int getCorrectAnswers() {
    return correctAnswers;
  }

  public int getTotalQuestions() {
    return totalQuestions;
  }

  public long getScoreEarned() {
    return scoreEarned;
  }
}
