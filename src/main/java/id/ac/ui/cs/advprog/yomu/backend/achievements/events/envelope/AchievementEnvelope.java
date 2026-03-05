package id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class AchievementEnvelope<TPayload> {
  private final UUID achievementId;
  private final AchievementType achievementType;
  private final int version;
  private final Instant occurredAt;
  private final TPayload payload;

  protected AchievementEnvelope(
      UUID achievementId,
      AchievementType achievementType,
      int version,
      Instant occurredAt,
      TPayload payload) {
    this.achievementId = achievementId;
    this.achievementType = achievementType;
    this.version = version;
    this.occurredAt = occurredAt;
    this.payload = payload;
  }

  public static <T> AchievementEnvelope<T> of(AchievementType type, int version, T payload) {
    return new AchievementEnvelope<>(UUID.randomUUID(), type, version, Instant.now(), payload);
  }
}
