package id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload;

import java.util.UUID;
import lombok.Getter;

/**
 * Payload pencapaian saat anggota Clan berada di Clan yang promosi ke Tier Diamond (tertinggi).
 * Dipublikasikan satu kali per anggota oleh {@code ClanPromotedAchievementListener} sehingga
 * listener achievement generik bisa menaikkan progress per-user tanpa perubahan.
 */
@Getter
public class ClanReachedDiamondPayload implements AchievementActivityPayload {
  private final UUID userId;
  private final UUID clanId;

  public ClanReachedDiamondPayload(UUID userId, UUID clanId) {
    this.userId = userId;
    this.clanId = clanId;
  }
}
