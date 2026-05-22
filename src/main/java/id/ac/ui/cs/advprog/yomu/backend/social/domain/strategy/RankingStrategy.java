package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import java.util.List;

public interface RankingStrategy {
  List<ClanScoreData> rank(List<ClanScoreData> clans);
}
