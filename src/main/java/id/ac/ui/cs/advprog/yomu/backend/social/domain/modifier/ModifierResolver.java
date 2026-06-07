package id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier;

public class ModifierResolver {

  public static final double PRODUCTIVITY_THRESHOLD = 0.5;
  public static final double LOW_ACCURACY_THRESHOLD = 0.5;

  public ScoreModifier resolve(ClanActivitySnapshot snapshot) {
    ScoreModifier chain = new IdentityModifier();

    if (isProductivityBuffActive(snapshot)) {
      chain = new ProductivityBuff(chain);
    }
    if (isLowAccuracyPenaltyActive(snapshot)) {
      chain = new LowAccuracyPenalty(chain);
    }

    return chain;
  }

  public ClanModifierStatus describe(ClanActivitySnapshot snapshot) {
    return new ClanModifierStatus(
        isProductivityBuffActive(snapshot),
        isLowAccuracyPenaltyActive(snapshot),
        snapshot.dailyMissionCompletionRate(),
        snapshot.averageAccuracy());
  }

  public boolean isProductivityBuffActive(ClanActivitySnapshot snapshot) {
    return snapshot.dailyMissionCompletionRate() >= PRODUCTIVITY_THRESHOLD;
  }

  public boolean isLowAccuracyPenaltyActive(ClanActivitySnapshot snapshot) {
    return snapshot.averageAccuracy() < LOW_ACCURACY_THRESHOLD;
  }
}
