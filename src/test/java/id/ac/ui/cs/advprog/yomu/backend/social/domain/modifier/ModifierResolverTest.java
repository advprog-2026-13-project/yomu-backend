package id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ModifierResolverTest {

  private final ModifierResolver resolver = new ModifierResolver();

  @Test
  void onlyProductivity_highCompletion_highAccuracy_apply1000_returns1200() {
    ScoreModifier modifier = resolver.resolve(new ClanActivitySnapshot(0.6, 0.8));
    assertEquals(1200L, modifier.apply(1000));
  }

  @Test
  void bothModifiers_highCompletion_lowAccuracy_apply1000_returns960() {
    ScoreModifier modifier = resolver.resolve(new ClanActivitySnapshot(0.6, 0.4));
    assertEquals(960L, modifier.apply(1000));
  }

  @Test
  void noModifier_lowCompletion_highAccuracy_apply1000_returns1000() {
    ScoreModifier modifier = resolver.resolve(new ClanActivitySnapshot(0.3, 0.8));
    assertEquals(1000L, modifier.apply(1000));
  }

  @Test
  void onlyLowAccuracy_lowCompletion_lowAccuracy_apply1000_returns800() {
    ScoreModifier modifier = resolver.resolve(new ClanActivitySnapshot(0.3, 0.4));
    assertEquals(800L, modifier.apply(1000));
  }

  @Test
  void boundary_completionExactly05_productivityActive() {
    ScoreModifier modifier = resolver.resolve(new ClanActivitySnapshot(0.5, 0.8));
    assertEquals(1200L, modifier.apply(1000));
  }

  @Test
  void boundary_accuracyExactly05_lowAccuracyNotActive() {
    ScoreModifier modifier = resolver.resolve(new ClanActivitySnapshot(0.3, 0.5));
    assertEquals(1000L, modifier.apply(1000));
  }

  @Test
  void describe_bothActive_reportsBuffAndDebuff() {
    ClanModifierStatus status = resolver.describe(new ClanActivitySnapshot(0.6, 0.4));
    assertTrue(status.productivityBuffActive());
    assertTrue(status.lowAccuracyPenaltyActive());
    assertTrue(status.hasAnyModifier());
    assertEquals(0.6, status.dailyMissionCompletionRate());
    assertEquals(0.4, status.averageAccuracy());
  }

  @Test
  void describe_onlyBuff_reportsBuffOnly() {
    ClanModifierStatus status = resolver.describe(new ClanActivitySnapshot(0.5, 0.8));
    assertTrue(status.productivityBuffActive());
    assertFalse(status.lowAccuracyPenaltyActive());
  }

  @Test
  void describe_onlyDebuff_reportsDebuffOnly() {
    ClanModifierStatus status = resolver.describe(new ClanActivitySnapshot(0.3, 0.4));
    assertFalse(status.productivityBuffActive());
    assertTrue(status.lowAccuracyPenaltyActive());
  }

  @Test
  void describe_none_reportsNoModifier() {
    ClanModifierStatus status = resolver.describe(new ClanActivitySnapshot(0.3, 0.8));
    assertFalse(status.hasAnyModifier());
  }
}
