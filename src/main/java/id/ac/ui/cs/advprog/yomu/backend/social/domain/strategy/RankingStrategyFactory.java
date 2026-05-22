package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.EnumMap;
import java.util.Map;

public class RankingStrategyFactory {

  private final Map<Tier, RankingStrategy> strategies;

  public RankingStrategyFactory(
      BronzeRankingStrategy bronze,
      SilverRankingStrategy silver,
      GoldRankingStrategy gold,
      DiamondRankingStrategy diamond) {
    EnumMap<Tier, RankingStrategy> map = new EnumMap<>(Tier.class);
    map.put(Tier.BRONZE, bronze);
    map.put(Tier.SILVER, silver);
    map.put(Tier.GOLD, gold);
    map.put(Tier.DIAMOND, diamond);
    this.strategies = map;
  }

  public RankingStrategy getStrategy(Tier tier) {
    RankingStrategy strategy = strategies.get(tier);
    if (strategy == null) {
      throw new IllegalStateException("No ranking strategy for tier: " + tier);
    }
    return strategy;
  }
}
