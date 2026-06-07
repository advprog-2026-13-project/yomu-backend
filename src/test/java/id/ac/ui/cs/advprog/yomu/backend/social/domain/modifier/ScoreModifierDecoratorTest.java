package id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ScoreModifierDecoratorTest {

  private final ScoreModifier base = new IdentityModifier();

  @Test
  void productivityBuff_apply1000_returns1200() {
    assertEquals(1200L, new ProductivityBuff(base).apply(1000));
  }

  @Test
  void lowAccuracyPenalty_apply1000_returns800() {
    assertEquals(800L, new LowAccuracyPenalty(base).apply(1000));
  }

  @Test
  void stacking_lowAccuracyOverProductivity_apply1000_returns960() {
    ScoreModifier stacked = new LowAccuracyPenalty(new ProductivityBuff(base));
    assertEquals(960L, stacked.apply(1000));
  }

  @Test
  void stacking_commutative_productivityOverLowAccuracy_apply1000_returns960() {
    ScoreModifier stacked = new ProductivityBuff(new LowAccuracyPenalty(base));
    assertEquals(960L, stacked.apply(1000));
  }

  @Test
  void rounding_lowAccuracyPenalty_apply999_rounds799() {
    assertEquals(799L, new LowAccuracyPenalty(base).apply(999));
  }

  @Test
  void rounding_productivityBuff_apply999_rounds1199_notFloor1198() {
    assertEquals(1199L, new ProductivityBuff(base).apply(999));
  }
}
