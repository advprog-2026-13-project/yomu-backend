package id.ac.ui.cs.advprog.yomu.backend.social.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanActivityProvider;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.SeasonStatePort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.event.ClanPromotedEvent;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ClanActivitySnapshot;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ModifierResolver;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy.RankingStrategy;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy.RankingStrategyFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class SeasonServiceTest {

  @Mock private ClanRepositoryPort clanRepository;
  @Mock private ClanMemberRepositoryPort clanMemberRepository;
  @Mock private RankingStrategyFactory strategyFactory;
  @Mock private RankingStrategy rankingStrategy;
  @Mock private ClanActivityProvider activityProvider;
  @Mock private ModifierResolver modifierResolver;
  @Mock private SeasonStatePort seasonState;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private SeasonService seasonService;

  @BeforeEach
  void setUp() {
    lenient().when(clanRepository.findByTierOrderByScoreDesc(any())).thenReturn(List.of());
    lenient().when(strategyFactory.getStrategy(any())).thenReturn(rankingStrategy);
    lenient().when(rankingStrategy.rank(anyList())).thenAnswer(inv -> inv.getArgument(0));
    lenient().when(clanMemberRepository.countByClanId(any())).thenReturn(1L);
    lenient().when(clanRepository.save(any(Clan.class))).thenAnswer(inv -> inv.getArgument(0));
    lenient()
        .when(activityProvider.getActivity(any()))
        .thenReturn(new ClanActivitySnapshot(0.0, 1.0));
    lenient().when(modifierResolver.resolve(any())).thenReturn(score -> score);
  }

  @Test
  void endSeason_eightClansInSilver_promotesTop2AndDemotesBottom1() {
    List<Clan> clans = clansInTier(Tier.SILVER, 800, 700, 600, 500, 400, 300, 200, 100);
    when(clanRepository.findByTierOrderByScoreDesc(Tier.SILVER)).thenReturn(clans);

    seasonService.endSeason();

    assertEquals(Tier.GOLD, clans.get(0).getTier());
    assertEquals(Tier.GOLD, clans.get(1).getTier());
    for (int i = 2; i <= 6; i++) assertEquals(Tier.SILVER, clans.get(i).getTier());
    assertEquals(Tier.BRONZE, clans.get(7).getTier());
  }

  @Test
  void endSeason_sevenClansInSilver_floorGivesOnePromoteAndOneDemote() {
    List<Clan> clans = clansInTier(Tier.SILVER, 700, 600, 500, 400, 300, 200, 100);
    when(clanRepository.findByTierOrderByScoreDesc(Tier.SILVER)).thenReturn(clans);

    seasonService.endSeason();

    assertEquals(Tier.GOLD, clans.get(0).getTier());
    for (int i = 1; i <= 5; i++) assertEquals(Tier.SILVER, clans.get(i).getTier());
    assertEquals(Tier.BRONZE, clans.get(6).getTier());
  }

  @Test
  void endSeason_threeClansInSilver_guardEnsuresOnePromoteAndOneDemote() {
    List<Clan> clans = clansInTier(Tier.SILVER, 300, 200, 100);
    when(clanRepository.findByTierOrderByScoreDesc(Tier.SILVER)).thenReturn(clans);

    seasonService.endSeason();

    assertEquals(Tier.GOLD, clans.get(0).getTier());
    assertEquals(Tier.SILVER, clans.get(1).getTier());
    assertEquals(Tier.BRONZE, clans.get(2).getTier());
  }

  @Test
  void endSeason_oneClanInSilver_noMovementGuardRequiresMoreThanOne() {
    Clan lone = clanWithScore(Tier.SILVER, 500);
    when(clanRepository.findByTierOrderByScoreDesc(Tier.SILVER)).thenReturn(List.of(lone));

    seasonService.endSeason();

    assertEquals(Tier.SILVER, lone.getTier());
  }

  @Test
  void endSeason_bronzeBottomClan_staysBronzeNeverBelowBoundary() {
    List<Clan> clans = clansInTier(Tier.BRONZE, 300, 200, 100);
    when(clanRepository.findByTierOrderByScoreDesc(Tier.BRONZE)).thenReturn(clans);

    seasonService.endSeason();

    assertEquals(Tier.SILVER, clans.get(0).getTier());
    assertEquals(Tier.BRONZE, clans.get(1).getTier());
    assertEquals(Tier.BRONZE, clans.get(2).getTier());
  }

  @Test
  void endSeason_diamondTopClan_staysDiamondNeverAboveBoundary() {
    List<Clan> clans = clansInTier(Tier.DIAMOND, 300, 200, 100);
    when(clanRepository.findByTierOrderByScoreDesc(Tier.DIAMOND)).thenReturn(clans);

    seasonService.endSeason();

    assertEquals(Tier.DIAMOND, clans.get(0).getTier());
    assertEquals(Tier.DIAMOND, clans.get(1).getTier());
    assertEquals(Tier.GOLD, clans.get(2).getTier());
  }

  @Test
  void endSeason_startsNewSeasonToResetDebuffWindow() {
    List<Clan> clans = clansInTier(Tier.SILVER, 300, 200, 100);
    when(clanRepository.findByTierOrderByScoreDesc(Tier.SILVER)).thenReturn(clans);

    seasonService.endSeason();

    verify(seasonState).startNewSeason(any(java.time.LocalDateTime.class));
  }

  @Test
  void endSeason_allScoresResetToZeroAfterProcessing() {
    List<Clan> clans = clansInTier(Tier.SILVER, 300, 200, 100);
    when(clanRepository.findByTierOrderByScoreDesc(Tier.SILVER)).thenReturn(clans);

    seasonService.endSeason();

    for (Clan clan : clans) {
      assertEquals(0L, clan.getScore(), "All scores must be reset to 0 after endSeason()");
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  void endSeason_rankingCalledWithPreResetScores_notWithZeros() {
    Clan clanA = clanWithScore(Tier.SILVER, 400);
    Clan clanB = clanWithScore(Tier.SILVER, 100);
    when(clanRepository.findByTierOrderByScoreDesc(Tier.SILVER)).thenReturn(List.of(clanA, clanB));

    ArgumentCaptor<List<ClanScoreData>> captor = ArgumentCaptor.forClass(List.class);
    when(rankingStrategy.rank(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

    seasonService.endSeason();
    List<ClanScoreData> silverInput =
        captor.getAllValues().stream()
            .filter(l -> !l.isEmpty())
            .findFirst()
            .orElseThrow(() -> new AssertionError("rank() was never called with SILVER data"));
    assertTrue(
        silverInput.stream().allMatch(d -> d.score() > 0),
        "ClanScoreData passed to rank() must carry pre-reset scores (>0), not zeros");
  }

  @Test
  void endSeason_clanPromotedToDiamond_publishesClanPromotedEvent() {
    List<Clan> clans = clansInTier(Tier.GOLD, 300, 100);
    when(clanRepository.findByTierOrderByScoreDesc(Tier.GOLD)).thenReturn(clans);

    UUID promotedClanId = clans.get(0).getId();
    UUID member1 = UUID.randomUUID();
    UUID member2 = UUID.randomUUID();
    when(clanMemberRepository.findByClanId(promotedClanId))
        .thenReturn(
            List.of(
                ClanMember.join(promotedClanId, member1),
                ClanMember.join(promotedClanId, member2)));

    seasonService.endSeason();

    assertEquals(Tier.DIAMOND, clans.get(0).getTier());

    ArgumentCaptor<ClanPromotedEvent> captor = ArgumentCaptor.forClass(ClanPromotedEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());
    ClanPromotedEvent event = captor.getValue();
    assertEquals(Tier.DIAMOND, event.newTier());
    assertEquals(promotedClanId, event.clanId());
    assertEquals(clans.get(0).getName(), event.clanName());
    assertEquals(List.of(member1, member2), event.memberUserIds());
  }

  @Test
  void endSeason_promotionToNonDiamond_doesNotPublishClanPromotedEvent() {
    List<Clan> clans = clansInTier(Tier.SILVER, 300, 100);
    when(clanRepository.findByTierOrderByScoreDesc(Tier.SILVER)).thenReturn(clans);

    seasonService.endSeason();

    assertEquals(Tier.GOLD, clans.get(0).getTier());
    verify(eventPublisher, never()).publishEvent(any(ClanPromotedEvent.class));
  }

  @Test
  void endSeason_clanStaysInDiamond_publishesClanPromotedEvent() {
    List<Clan> clans = clansInTier(Tier.DIAMOND, 300, 100);
    when(clanRepository.findByTierOrderByScoreDesc(Tier.DIAMOND)).thenReturn(clans);

    UUID stayingClanId = clans.get(0).getId();
    UUID member1 = UUID.randomUUID();
    when(clanMemberRepository.findByClanId(stayingClanId))
        .thenReturn(List.of(ClanMember.join(stayingClanId, member1)));

    seasonService.endSeason();

    assertEquals(Tier.DIAMOND, clans.get(0).getTier());
    assertEquals(Tier.GOLD, clans.get(1).getTier());

    ArgumentCaptor<ClanPromotedEvent> captor = ArgumentCaptor.forClass(ClanPromotedEvent.class);
    verify(eventPublisher, times(1)).publishEvent(captor.capture());
    ClanPromotedEvent event = captor.getValue();
    assertEquals(Tier.DIAMOND, event.newTier());
    assertEquals(stayingClanId, event.clanId());
    assertTrue(event.memberUserIds().contains(member1));
  }

  @Test
  void endSeason_bothNewAndExistingDiamondClans_bothGetEvents() {
    // Gold butuh ≥2 clan agar guard (total < 2) tidak skip promotion/demotion.
    // Satu Diamond clan sudah ada dan tetap (1 clan → guard skip, jadi tetap di Diamond).
    UUID existingDiamondId = UUID.randomUUID();
    Clan existingDiamond = new Clan();
    existingDiamond.setId(existingDiamondId);
    existingDiamond.setName("Existing-Diamond");
    existingDiamond.setTier(Tier.DIAMOND);
    existingDiamond.setScore(500L);

    UUID newDiamondId = UUID.randomUUID();
    Clan topGold = new Clan();
    topGold.setId(newDiamondId);
    topGold.setName("Top-Gold");
    topGold.setTier(Tier.GOLD);
    topGold.setScore(400L);

    Clan bottomGold = clanWithScore(Tier.GOLD, 100);

    when(clanRepository.findByTierOrderByScoreDesc(Tier.DIAMOND))
        .thenReturn(List.of(existingDiamond));
    when(clanRepository.findByTierOrderByScoreDesc(Tier.GOLD))
        .thenReturn(List.of(topGold, bottomGold));

    UUID memberA = UUID.randomUUID();
    UUID memberB = UUID.randomUUID();
    when(clanMemberRepository.findByClanId(existingDiamondId))
        .thenReturn(List.of(ClanMember.join(existingDiamondId, memberA)));
    when(clanMemberRepository.findByClanId(newDiamondId))
        .thenReturn(List.of(ClanMember.join(newDiamondId, memberB)));

    seasonService.endSeason();

    assertEquals(Tier.DIAMOND, existingDiamond.getTier());
    assertEquals(Tier.DIAMOND, topGold.getTier());
    assertEquals(Tier.SILVER, bottomGold.getTier());

    ArgumentCaptor<ClanPromotedEvent> captor = ArgumentCaptor.forClass(ClanPromotedEvent.class);
    verify(eventPublisher, times(2)).publishEvent(captor.capture());

    List<UUID> publishedClanIds =
        captor.getAllValues().stream().map(ClanPromotedEvent::clanId).toList();
    assertTrue(
        publishedClanIds.contains(existingDiamondId), "existing Diamond clan harus dapat event");
    assertTrue(
        publishedClanIds.contains(newDiamondId), "Gold yang naik ke Diamond harus dapat event");
  }

  private List<Clan> clansInTier(Tier tier, int... scores) {
    List<Clan> result = new ArrayList<>();
    for (int score : scores) result.add(clanWithScore(tier, score));
    return result;
  }

  private Clan clanWithScore(Tier tier, int score) {
    Clan clan = new Clan();
    clan.setId(UUID.randomUUID());
    clan.setName("Clan-" + score);
    clan.setTier(tier);
    clan.setScore((long) score);
    return clan;
  }
}
