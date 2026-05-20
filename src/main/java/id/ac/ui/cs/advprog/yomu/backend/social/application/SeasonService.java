package id.ac.ui.cs.advprog.yomu.backend.social.application;

import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy.RankingStrategyFactory;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SeasonService {

  // Rates per design doc §4 "Promosi & Degradasi Musiman" — asymmetric by design
  private static final double PROMOTION_RATE = 0.25;
  private static final double DEMOTION_RATE = 0.15;
  // floor(count × rate) can be 0 for small tiers; guard ensures at least this many move
  // each way when the tier has more than one clan
  private static final int MIN_MOVEMENT = 1;

  private final ClanRepositoryPort clanRepository;
  private final ClanMemberRepositoryPort clanMemberRepository;
  private final RankingStrategyFactory strategyFactory;

  public SeasonService(
      ClanRepositoryPort clanRepository,
      ClanMemberRepositoryPort clanMemberRepository,
      RankingStrategyFactory strategyFactory) {
    this.clanRepository = clanRepository;
    this.clanMemberRepository = clanMemberRepository;
    this.strategyFactory = strategyFactory;
  }

  public void endSeason() {
    // Phase 1: snapshot rankings for ALL tiers before any mutations.
    // ClanScoreData is built here so rank() always sees pre-reset scores.
    Map<Tier, List<Clan>> clansByTier = new EnumMap<>(Tier.class);
    Map<Tier, List<ClanScoreData>> rankedByTier = new EnumMap<>(Tier.class);
    List<Clan> allClans = new ArrayList<>();

    for (Tier tier : Tier.values()) {
      List<Clan> clans = clanRepository.findByTierOrderByScoreDesc(tier);
      clansByTier.put(tier, clans);
      allClans.addAll(clans);
      if (!clans.isEmpty()) {
        List<ClanScoreData> scoreData = buildScoreData(clans);
        rankedByTier.put(tier, strategyFactory.getStrategy(tier).rank(scoreData));
      }
    }

    // Phase 2: apply tier promotions and demotions based on the snapshots
    for (Tier tier : Tier.values()) {
      List<ClanScoreData> ranked = rankedByTier.getOrDefault(tier, List.of());
      int total = ranked.size();
      if (total < 2) continue; // guard: movement requires more than one clan in tier

      Map<UUID, Clan> clanMap = new HashMap<>();
      for (Clan c : clansByTier.get(tier)) clanMap.put(c.getId(), c);

      int promoteCount = Math.max(MIN_MOVEMENT, (int) Math.floor(total * PROMOTION_RATE));
      int demoteCount = Math.max(MIN_MOVEMENT, (int) Math.floor(total * DEMOTION_RATE));

      for (int i = 0; i < promoteCount; i++) {
        Clan clan = clanMap.get(ranked.get(i).clanId());
        clan.setTier(clan.getTier().nextTier()); // DIAMOND.nextTier()=DIAMOND: no-op at boundary
        clanRepository.save(clan);
      }

      for (int i = 0; i < demoteCount; i++) {
        Clan clan = clanMap.get(ranked.get(total - 1 - i).clanId());
        clan.setTier(clan.getTier().previousTier()); // BRONZE.previousTier()=BRONZE: no-op at boundary
        clanRepository.save(clan);
      }
    }

    // Phase 3: reset all scores after ranking so the new season starts fair
    for (Clan clan : allClans) {
      clan.setScore(0L);
      clanRepository.save(clan);
    }
  }

  private List<ClanScoreData> buildScoreData(List<Clan> clans) {
    return clans.stream()
        .map(
            c ->
                new ClanScoreData(
                    c.getId(),
                    c.getName(),
                    c.getTier(),
                    c.getScore(),
                    clanMemberRepository.countByClanId(c.getId())))
        .toList();
  }
}
