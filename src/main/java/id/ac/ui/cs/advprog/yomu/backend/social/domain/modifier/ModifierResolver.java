package id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier;

public class ModifierResolver {

  public static final double PRODUCTIVITY_THRESHOLD = 0.5;
  public static final double LOW_ACCURACY_THRESHOLD = 0.5;

  public ScoreModifier resolve(ClanActivitySnapshot snapshot) {
    ScoreModifier chain = new IdentityModifier();

    if (snapshot.dailyMissionCompletionRate() >= PRODUCTIVITY_THRESHOLD) {
      chain = new ProductivityBuff(chain);
    }
    if (snapshot.averageAccuracy() < LOW_ACCURACY_THRESHOLD) {
      chain = new LowAccuracyPenalty(chain);
    }

    return chain;
  }
}
