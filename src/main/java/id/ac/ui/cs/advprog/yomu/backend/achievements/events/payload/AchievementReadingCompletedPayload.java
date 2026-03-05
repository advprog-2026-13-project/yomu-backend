package id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload;

import java.util.UUID;
import lombok.Getter;

@Getter
public class AchievementReadingCompletedPayload {
  private final UUID userId;
  private final UUID readingId;
  private final int durationSec;

  public AchievementReadingCompletedPayload(UUID userId, UUID readingId, int durationSec) {
    this.userId = userId;
    this.readingId = readingId;
    this.durationSec = durationSec;
  }
}
