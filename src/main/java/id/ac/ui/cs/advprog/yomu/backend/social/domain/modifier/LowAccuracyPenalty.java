package id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier;

public class LowAccuracyPenalty extends ScoreModifierDecorator {

  public static final double MULTIPLIER = 0.8;

  public LowAccuracyPenalty(ScoreModifier inner) {
    super(inner);
  }

  @Override
  public long apply(long baseScore) {
    return Math.round(inner.apply(baseScore) * MULTIPLIER);
  }
}
