package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import java.util.Comparator;
import java.util.List;

public class DiamondRankingStrategy implements RankingStrategy {

  // Prevents small clans from exploiting high averages; see design doc §4 "Formula Ranking per
  // Tier"
  private static final int MIN_MEMBERS_FOR_DIAMOND = 3;

  @Override
  public List<ClanScoreData> rank(List<ClanScoreData> clans) {
    return clans.stream()
        .sorted(
            Comparator.comparingDouble(
                    (ClanScoreData c) ->
                        c.memberCount() >= MIN_MEMBERS_FOR_DIAMOND
                            ? (double) c.score() / c.memberCount()
                            : 0)
                .reversed())
        .toList();
  }
}
