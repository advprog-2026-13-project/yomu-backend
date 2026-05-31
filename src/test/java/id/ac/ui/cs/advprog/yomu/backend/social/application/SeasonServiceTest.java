package id.ac.ui.cs.advprog.yomu.backend.social.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
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

@ExtendWith(MockitoExtension.class)
class SeasonServiceTest {

  @Mock private ClanRepositoryPort clanRepository;
  @Mock private ClanMemberRepositoryPort clanMemberRepository;
  @Mock private RankingStrategyFactory strategyFactory;
  @Mock private RankingStrategy rankingStrategy;

  @InjectMocks private SeasonService seasonService;

  @BeforeEach
  void setUp() {
    lenient().when(clanRepository.findByTierOrderByScoreDesc(any())).thenReturn(List.of());
    lenient().when(strategyFactory.getStrategy(any())).thenReturn(rankingStrategy);
    lenient().when(rankingStrategy.rank(anyList())).thenAnswer(inv -> inv.getArgument(0));
    lenient().when(clanMemberRepository.countByClanId(any())).thenReturn(1L);
    lenient().when(clanRepository.save(any(Clan.class))).thenAnswer(inv -> inv.getArgument(0));
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
