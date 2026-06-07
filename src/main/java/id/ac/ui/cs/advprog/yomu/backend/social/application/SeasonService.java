package id.ac.ui.cs.advprog.yomu.backend.social.application;

import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanActivityProvider;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.SeasonStatePort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.event.ClanPromotedEvent;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ModifierResolver;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy.RankingStrategyFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SeasonService {
  private static final double PROMOTION_RATE = 0.25;
  private static final double DEMOTION_RATE = 0.15;
  private static final int MIN_MOVEMENT = 1;

  private final ClanRepositoryPort clanRepository;
  private final ClanMemberRepositoryPort clanMemberRepository;
  private final RankingStrategyFactory strategyFactory;
  private final ClanActivityProvider activityProvider;
  private final ModifierResolver modifierResolver;
  private final SeasonStatePort seasonState;
  private final ApplicationEventPublisher eventPublisher;

  public SeasonService(
      ClanRepositoryPort clanRepository,
      ClanMemberRepositoryPort clanMemberRepository,
      RankingStrategyFactory strategyFactory,
      ClanActivityProvider activityProvider,
      ModifierResolver modifierResolver,
      SeasonStatePort seasonState,
      ApplicationEventPublisher eventPublisher) {
    this.clanRepository = clanRepository;
    this.clanMemberRepository = clanMemberRepository;
    this.strategyFactory = strategyFactory;
    this.activityProvider = activityProvider;
    this.modifierResolver = modifierResolver;
    this.seasonState = seasonState;
    this.eventPublisher = eventPublisher;
  }

  public void endSeason() {
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

    for (Tier tier : Tier.values()) {
      List<ClanScoreData> ranked = rankedByTier.getOrDefault(tier, List.of());
      int total = ranked.size();
      if (total < 2) continue;

      Map<UUID, Clan> clanMap = new HashMap<>();
      for (Clan c : clansByTier.get(tier)) clanMap.put(c.getId(), c);

      int promoteCount = Math.max(MIN_MOVEMENT, (int) Math.floor(total * PROMOTION_RATE));
      int demoteCount = Math.max(MIN_MOVEMENT, (int) Math.floor(total * DEMOTION_RATE));

      for (int i = 0; i < promoteCount; i++) {
        Clan clan = clanMap.get(ranked.get(i).clanId());
        clan.setTier(clan.getTier().nextTier());
        clanRepository.save(clan);
      }

      for (int i = 0; i < demoteCount; i++) {
        Clan clan = clanMap.get(ranked.get(total - 1 - i).clanId());
        clan.setTier(clan.getTier().previousTier());
        clanRepository.save(clan);
      }
    }

    for (Clan clan : allClans) {
      if (clan.getTier() == Tier.DIAMOND) {
        List<UUID> memberUserIds =
            clanMemberRepository.findByClanId(clan.getId()).stream()
                .map(ClanMember::getUserId)
                .toList();
        eventPublisher.publishEvent(
            new ClanPromotedEvent(
                clan.getId(), clan.getName(), Tier.DIAMOND, clan.getLeaderId(), memberUserIds));
      }
    }

    for (Clan clan : allClans) {
      clan.setScore(0L);
      clanRepository.save(clan);
    }

    // Tandai season baru: perhitungan debuff (akurasi kuis) hanya menghitung
    // attempt sejak titik ini, sehingga debuff ter-reset untuk season berikutnya.
    seasonState.startNewSeason(LocalDateTime.now());
  }

  private List<ClanScoreData> buildScoreData(List<Clan> clans) {
    return clans.stream()
        .map(
            c -> {
              long modifiedScore =
                  modifierResolver
                      .resolve(activityProvider.getActivity(c.getId()))
                      .apply(c.getScore());
              return new ClanScoreData(
                  c.getId(),
                  c.getName(),
                  c.getTier(),
                  modifiedScore,
                  clanMemberRepository.countByClanId(c.getId()));
            })
        .toList();
  }
}
