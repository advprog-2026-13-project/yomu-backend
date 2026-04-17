package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BronzeRankingStrategy implements RankingStrategy {

  @Override
  public List<ClanScoreData> rank(List<ClanScoreData> clans) {
    return clans.stream()
        .sorted(Comparator.comparingLong(ClanScoreData::score).reversed())
        .toList();
  }
}