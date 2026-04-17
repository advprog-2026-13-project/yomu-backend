package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import org.springframework.stereotype.Component;

@Component
public class RankingStrategyFactory {

  private final BronzeRankingStrategy bronzeRankingStrategy;
  private final DiamondRankingStrategy diamondRankingStrategy;

  public RankingStrategyFactory(
      BronzeRankingStrategy bronzeRankingStrategy, DiamondRankingStrategy diamondRankingStrategy) {
    this.bronzeRankingStrategy = bronzeRankingStrategy;
    this.diamondRankingStrategy = diamondRankingStrategy;
  }

  public RankingStrategy getStrategy(Tier tier) {
    if (tier == Tier.DIAMOND) {
      return diamondRankingStrategy;
    }
    return bronzeRankingStrategy;
  }
}
