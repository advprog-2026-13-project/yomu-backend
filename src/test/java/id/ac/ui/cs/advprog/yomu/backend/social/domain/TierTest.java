package id.ac.ui.cs.advprog.yomu.backend.social.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TierTest {

  @Test
  void nextTier_fromBronze_returnsSilver() {
    assertEquals(Tier.SILVER, Tier.BRONZE.nextTier());
  }

  @Test
  void nextTier_fromSilver_returnsGold() {
    assertEquals(Tier.GOLD, Tier.SILVER.nextTier());
  }

  @Test
  void nextTier_fromGold_returnsDiamond() {
    assertEquals(Tier.DIAMOND, Tier.GOLD.nextTier());
  }

  @Test
  void nextTier_fromDiamond_returnsDiamond_atTopCap() {
    assertEquals(Tier.DIAMOND, Tier.DIAMOND.nextTier());
  }

  @Test
  void previousTier_fromDiamond_returnsGold() {
    assertEquals(Tier.GOLD, Tier.DIAMOND.previousTier());
  }

  @Test
  void previousTier_fromGold_returnsSilver() {
    assertEquals(Tier.SILVER, Tier.GOLD.previousTier());
  }

  @Test
  void previousTier_fromSilver_returnsBronze() {
    assertEquals(Tier.BRONZE, Tier.SILVER.previousTier());
  }

  @Test
  void previousTier_fromBronze_returnsBronze_atBottomCap() {
    assertEquals(Tier.BRONZE, Tier.BRONZE.previousTier());
  }
}
