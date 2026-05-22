package id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier;

public class DoubleXpBuff extends ScoreModifierDecorator {

  public static final double MULTIPLIER = 2.0;

  public DoubleXpBuff(ScoreModifier inner) {
    super(inner);
  }

  @Override
  public long apply(long baseScore) {
    return Math.round(inner.apply(baseScore) * MULTIPLIER);
  }
}
