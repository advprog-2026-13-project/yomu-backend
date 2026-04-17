package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DiamondRankingStrategy implements RankingStrategy {

  @Override
  public List<ClanScoreData> rank(List<ClanScoreData> clans) {
    return clans.stream()
        .sorted(
            Comparator.comparingDouble(
                    (ClanScoreData c) -> c.memberCount() > 0 ? (double) c.score() / c.memberCount() : 0)
                .reversed())
        .toList();
  }
}