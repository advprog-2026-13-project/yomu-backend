package id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier;

public class ProductivityBuff extends ScoreModifierDecorator {

  public static final double MULTIPLIER = 1.2;

  public ProductivityBuff(ScoreModifier inner) {
    super(inner);
  }

  @Override
  public long apply(long baseScore) {
    return Math.round(inner.apply(baseScore) * MULTIPLIER);
  }
}
