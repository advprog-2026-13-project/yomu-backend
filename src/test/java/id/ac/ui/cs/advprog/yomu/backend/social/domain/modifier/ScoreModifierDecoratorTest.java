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
  void doubleXpBuff_apply1000_returns2000() {
    assertEquals(2000L, new DoubleXpBuff(base).apply(1000));
  }

  @Test
  void stacking_lowAccuracyOverProductivity_apply1000_returns960() {
    // 1000 × 1.2 = 1200, × 0.8 = 960
    ScoreModifier stacked = new LowAccuracyPenalty(new ProductivityBuff(base));
    assertEquals(960L, stacked.apply(1000));
  }

  @Test
  void stacking_commutative_productivityOverLowAccuracy_apply1000_returns960() {
    // 1000 × 0.8 = 800, × 1.2 = 960 — urutan dibalik, hasil sama
    ScoreModifier stacked = new ProductivityBuff(new LowAccuracyPenalty(base));
    assertEquals(960L, stacked.apply(1000));
  }

  @Test
  void rounding_lowAccuracyPenalty_apply999_rounds799() {
    // 999 × 0.8 = 799.2 → Math.round → 799 (bukan 800)
    assertEquals(799L, new LowAccuracyPenalty(base).apply(999));
  }

  @Test
  void rounding_productivityBuff_apply999_rounds1199_notFloor1198() {
    // 999 × 1.2 = 1198.8 → Math.round → 1199; floor() → 1198
    // Test ini membedakan Math.round dari floor — keduanya beda di sini
    assertEquals(1199L, new ProductivityBuff(base).apply(999));
  }
}
