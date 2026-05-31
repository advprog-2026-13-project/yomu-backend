package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import java.util.Comparator;
import java.util.List;

public class GoldRankingStrategy implements RankingStrategy {

  private static final double WEIGHT = 0.5;

  @Override
  public List<ClanScoreData> rank(List<ClanScoreData> clans) {
    return clans.stream()
        .sorted(
            Comparator.comparingDouble(
                    (ClanScoreData c) ->
                        c.memberCount() > 0
                            ? c.score() * WEIGHT + (double) c.score() / c.memberCount() * WEIGHT
                            : 0.0)
                .reversed())
        .toList();
  }
}
