package id.ac.ui.cs.advprog.yomu.backend.social.application;

import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy.RankingStrategyFactory;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.ClanRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LeaderboardService {

  private final ClanRepository clanRepository;
  private final ClanMemberRepository clanMemberRepository;
  private final RankingStrategyFactory strategyFactory;

  public LeaderboardService(
      ClanRepository clanRepository,
      ClanMemberRepository clanMemberRepository,
      RankingStrategyFactory strategyFactory) {
    this.clanRepository = clanRepository;
    this.clanMemberRepository = clanMemberRepository;
    this.strategyFactory = strategyFactory;
  }

  public List<LeaderboardEntryResponse> getLeaderboard(Tier tier) {
    List<Clan> clans = clanRepository.findByTierOrderByScoreDesc(tier);

    List<ClanScoreData> scoreData =
        clans.stream()
            .map(
                clan ->
                    new ClanScoreData(
                        clan.getId(),
                        clan.getName(),
                        clan.getTier(),
                        clan.getScore(),
                        clanMemberRepository.countByClanId(clan.getId())))
            .toList();

    List<ClanScoreData> ranked = strategyFactory.getStrategy(tier).rank(scoreData);

    List<LeaderboardEntryResponse> result = new ArrayList<>();
    for (int i = 0; i < ranked.size(); i++) {
      ClanScoreData data = ranked.get(i);
      result.add(
          new LeaderboardEntryResponse(i + 1, data.clanId(), data.clanName(), data.score(), data.tier()));
    }
    return result;
  }
}