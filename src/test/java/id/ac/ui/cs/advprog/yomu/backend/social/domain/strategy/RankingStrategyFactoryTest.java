package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RankingStrategyFactoryTest {

  private BronzeRankingStrategy bronze;
  private SilverRankingStrategy silver;
  private GoldRankingStrategy gold;
  private DiamondRankingStrategy diamond;
  private RankingStrategyFactory factory;

  @BeforeEach
  void setUp() {
    bronze = new BronzeRankingStrategy();
    silver = new SilverRankingStrategy();
    gold = new GoldRankingStrategy();
    diamond = new DiamondRankingStrategy();
    factory = new RankingStrategyFactory(bronze, silver, gold, diamond);
  }

  @Test
  void getStrategy_forBronze_returnsBronzeStrategy() {
    assertSame(bronze, factory.getStrategy(Tier.BRONZE));
  }

  @Test
  void getStrategy_forSilver_returnsSilverStrategy() {
    assertNotNull(factory.getStrategy(Tier.SILVER));
    assertSame(silver, factory.getStrategy(Tier.SILVER));
    assertInstanceOf(SilverRankingStrategy.class, factory.getStrategy(Tier.SILVER));
  }

  @Test
  void getStrategy_forGold_returnsGoldStrategy() {
    assertNotNull(factory.getStrategy(Tier.GOLD));
    assertSame(gold, factory.getStrategy(Tier.GOLD));
    assertInstanceOf(GoldRankingStrategy.class, factory.getStrategy(Tier.GOLD));
  }

  @Test
  void getStrategy_forDiamond_returnsDiamondStrategy() {
    assertSame(diamond, factory.getStrategy(Tier.DIAMOND));
  }
}
