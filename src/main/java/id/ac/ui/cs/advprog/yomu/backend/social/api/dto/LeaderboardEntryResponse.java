package id.ac.ui.cs.advprog.yomu.backend.social.api.dto;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.UUID;

public class LeaderboardEntryResponse {

  private final int rank;
  private final UUID clanId;
  private final String clanName;
  private final long score;
  private final Tier tier;

  public LeaderboardEntryResponse(int rank, UUID clanId, String clanName, long score, Tier tier) {
    this.rank = rank;
    this.clanId = clanId;
    this.clanName = clanName;
    this.score = score;
    this.tier = tier;
  }

  public int getRank() { return rank; }
  public UUID getClanId() { return clanId; }
  public String getClanName() { return clanName; }
  public long getScore() { return score; }
  public Tier getTier() { return tier; }
}