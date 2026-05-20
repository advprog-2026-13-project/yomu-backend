package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.config;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy.BronzeRankingStrategy;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy.DiamondRankingStrategy;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy.RankingStrategyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RankingStrategyConfig {

  @Bean
  public RankingStrategyFactory rankingStrategyFactory() {
    return new RankingStrategyFactory(new BronzeRankingStrategy(), new DiamondRankingStrategy());
  }
}
