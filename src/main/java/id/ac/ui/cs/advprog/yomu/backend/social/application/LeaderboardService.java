package id.ac.ui.cs.advprog.yomu.backend.social.application;

import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanActivityProvider;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ClanActivitySnapshot;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ClanModifierStatus;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ModifierResolver;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy.RankingStrategyFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LeaderboardService {

  private final ClanRepositoryPort clanRepository;
  private final ClanMemberRepositoryPort clanMemberRepository;
  private final RankingStrategyFactory strategyFactory;
  private final ClanActivityProvider activityProvider;
  private final ModifierResolver modifierResolver;

  public LeaderboardService(
      ClanRepositoryPort clanRepository,
      ClanMemberRepositoryPort clanMemberRepository,
      RankingStrategyFactory strategyFactory,
      ClanActivityProvider activityProvider,
      ModifierResolver modifierResolver) {
    this.clanRepository = clanRepository;
    this.clanMemberRepository = clanMemberRepository;
    this.strategyFactory = strategyFactory;
    this.activityProvider = activityProvider;
    this.modifierResolver = modifierResolver;
  }

  public List<LeaderboardEntryResponse> getLeaderboard(Tier tier) {
    List<Clan> clans = clanRepository.findByTierOrderByScoreDesc(tier);

    Map<UUID, ClanModifierStatus> modifiersByClan = new HashMap<>();
    List<ClanScoreData> scoreData =
        clans.stream()
            .map(
                clan -> {
                  ClanActivitySnapshot snapshot = activityProvider.getActivity(clan.getId());
                  modifiersByClan.put(clan.getId(), modifierResolver.describe(snapshot));
                  return new ClanScoreData(
                      clan.getId(),
                      clan.getName(),
                      clan.getTier(),
                      modifierResolver.resolve(snapshot).apply(clan.getScore()),
                      clanMemberRepository.countByClanId(clan.getId()));
                })
            .toList();

    List<ClanScoreData> ranked = strategyFactory.getStrategy(tier).rank(scoreData);

    List<LeaderboardEntryResponse> result = new ArrayList<>();
    for (int i = 0; i < ranked.size(); i++) {
      ClanScoreData data = ranked.get(i);
      ClanModifierStatus mod =
          modifiersByClan.getOrDefault(data.clanId(), ClanModifierStatus.none());
      result.add(
          new LeaderboardEntryResponse(
              i + 1,
              data.clanId(),
              data.clanName(),
              data.score(),
              data.tier(),
              mod.productivityBuffActive(),
              mod.lowAccuracyPenaltyActive()));
    }
    return result;
  }
}
