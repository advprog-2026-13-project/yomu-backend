package id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload;

import java.util.UUID;
import lombok.Getter;

// One instance published per clan member so the generic listener can increment per-user progress.
@Getter
public class ClanReachedDiamondPayload implements AchievementActivityPayload {
  private final UUID userId;
  private final UUID clanId;

  public ClanReachedDiamondPayload(UUID userId, UUID clanId) {
    this.userId = userId;
    this.clanId = clanId;
  }
}
