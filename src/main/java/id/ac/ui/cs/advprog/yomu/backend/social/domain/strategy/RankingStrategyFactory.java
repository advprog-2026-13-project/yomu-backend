package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.EnumMap;
import java.util.Map;

public class RankingStrategyFactory {

  private final Map<Tier, RankingStrategy> strategies;

  public RankingStrategyFactory(
      BronzeRankingStrategy bronzeRankingStrategy, DiamondRankingStrategy diamondRankingStrategy) {
    EnumMap<Tier, RankingStrategy> map = new EnumMap<>(Tier.class);
    map.put(Tier.BRONZE, bronzeRankingStrategy);
    map.put(Tier.DIAMOND, diamondRankingStrategy);
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
