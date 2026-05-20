package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RankingStrategyFactoryTest {

  private BronzeRankingStrategy bronze;
  private DiamondRankingStrategy diamond;
  private RankingStrategyFactory factory;

  @BeforeEach
  void setUp() {
    bronze = new BronzeRankingStrategy();
    diamond = new DiamondRankingStrategy();
    factory = new RankingStrategyFactory(bronze, diamond);
  }

  @Test
  void getStrategy_forBronze_returnsBronzeStrategy() {
    assertSame(bronze, factory.getStrategy(Tier.BRONZE));
  }

  @Test
  void getStrategy_forDiamond_returnsDiamondStrategy() {
    assertSame(diamond, factory.getStrategy(Tier.DIAMOND));
  }

  @Test
  void getStrategy_forSilver_throwsIllegalStateException() {
    IllegalStateException ex =
        assertThrows(IllegalStateException.class, () -> factory.getStrategy(Tier.SILVER));
    assertTrue(
        ex.getMessage().contains("SILVER"),
        "Pesan exception harus menyebut nama Tier supaya debug-friendly. Pesan aktual: "
            + ex.getMessage());
  }

  @Test
  void getStrategy_forUnsupportedTier_throwsIllegalStateException() {
    IllegalStateException ex =
        assertThrows(IllegalStateException.class, () -> factory.getStrategy(Tier.GOLD));
    assertTrue(
        ex.getMessage().contains("GOLD"),
        "Pesan exception harus menyebut nama Tier supaya debug-friendly. Pesan aktual: "
            + ex.getMessage());
  }
}
