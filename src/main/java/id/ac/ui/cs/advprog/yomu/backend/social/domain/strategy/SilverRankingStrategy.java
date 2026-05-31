package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import java.util.Comparator;
import java.util.List;

public class SilverRankingStrategy implements RankingStrategy {
  private static final long MEMBER_BONUS = 10L;

  @Override
  public List<ClanScoreData> rank(List<ClanScoreData> clans) {
    return clans.stream()
        .sorted(
            Comparator.comparingLong(
                    (ClanScoreData c) -> c.score() + c.memberCount() * MEMBER_BONUS)
                .reversed())
        .toList();
  }
}
