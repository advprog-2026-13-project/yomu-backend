package id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ModifierResolverTest {

  private final ModifierResolver resolver = new ModifierResolver();

  @Test
  void onlyProductivity_highCompletion_highAccuracy_apply1000_returns1200() {
    // completion 0.6 >= 0.5 -> Productivity aktif
    // accuracy  0.8 >= 0.5 -> Low Accuracy TIDAK aktif
    ScoreModifier modifier = resolver.resolve(new ClanActivitySnapshot(0.6, 0.8));
    assertEquals(1200L, modifier.apply(1000));
  }

  @Test
  void bothModifiers_highCompletion_lowAccuracy_apply1000_returns960() {
    // completion 0.6 >= 0.5 -> Productivity aktif (×1.2)
    // accuracy  0.4 <  0.5 -> Low Accuracy aktif  (×0.8)
    // 1000 × 1.2 × 0.8 = 960
    ScoreModifier modifier = resolver.resolve(new ClanActivitySnapshot(0.6, 0.4));
    assertEquals(960L, modifier.apply(1000));
  }

  @Test
  void noModifier_lowCompletion_highAccuracy_apply1000_returns1000() {
    // completion 0.3 <  0.5 -> Productivity TIDAK aktif
    // accuracy  0.8 >= 0.5 -> Low Accuracy TIDAK aktif
    ScoreModifier modifier = resolver.resolve(new ClanActivitySnapshot(0.3, 0.8));
    assertEquals(1000L, modifier.apply(1000));
  }

  @Test
  void onlyLowAccuracy_lowCompletion_lowAccuracy_apply1000_returns800() {
    // completion 0.3 <  0.5 -> Productivity TIDAK aktif
    // accuracy  0.4 <  0.5 -> Low Accuracy aktif (×0.8)
    ScoreModifier modifier = resolver.resolve(new ClanActivitySnapshot(0.3, 0.4));
    assertEquals(800L, modifier.apply(1000));
  }

  @Test
  void boundary_completionExactly05_productivityActive() {
    // tepat 0.5 -> >= 0.5 -> Productivity AKTIF
    ScoreModifier modifier = resolver.resolve(new ClanActivitySnapshot(0.5, 0.8));
    assertEquals(1200L, modifier.apply(1000));
  }

  @Test
  void boundary_accuracyExactly05_lowAccuracyNotActive() {
    // tepat 0.5 -> NOT < 0.5 -> Low Accuracy TIDAK aktif
    ScoreModifier modifier = resolver.resolve(new ClanActivitySnapshot(0.3, 0.5));
    assertEquals(1000L, modifier.apply(1000));
  }
}
