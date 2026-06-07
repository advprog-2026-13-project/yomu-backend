package id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier;

public record ClanModifierStatus(
    boolean productivityBuffActive,
    boolean lowAccuracyPenaltyActive,
    double dailyMissionCompletionRate,
    double averageAccuracy) {

  public static ClanModifierStatus none() {
    return new ClanModifierStatus(false, false, 0.0, 1.0);
  }

  public boolean hasAnyModifier() {
    return productivityBuffActive || lowAccuracyPenaltyActive;
  }
}
