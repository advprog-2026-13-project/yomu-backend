package id.ac.ui.cs.advprog.yomu.backend.social.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanActivityProvider;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ClanActivitySnapshot;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ClanModifierStatus;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.IdentityModifier;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ModifierResolver;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy.RankingStrategy;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy.RankingStrategyFactory;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

  @Mock private ClanRepositoryPort clanRepository;
  @Mock private ClanMemberRepositoryPort clanMemberRepository;
  @Mock private RankingStrategyFactory strategyFactory;
  @Mock private RankingStrategy rankingStrategy;
  @Mock private ClanActivityProvider activityProvider;
  @Mock private ModifierResolver modifierResolver;

  @InjectMocks private LeaderboardService leaderboardService;

  @Test
  void getLeaderboard_happyPath_buildsScoreDataDelegatesToStrategyAndAssignsRanksByOrder() {
    UUID c1Id = UUID.randomUUID();
    UUID c2Id = UUID.randomUUID();

    Clan c1 = new Clan();
    c1.setId(c1Id);
    c1.setName("Alpha");
    c1.setTier(Tier.BRONZE);
    c1.setScore(100L);

    Clan c2 = new Clan();
    c2.setId(c2Id);
    c2.setName("Beta");
    c2.setTier(Tier.BRONZE);
    c2.setScore(50L);

    when(clanRepository.findByTierOrderByScoreDesc(Tier.BRONZE)).thenReturn(List.of(c1, c2));
    when(clanMemberRepository.countByClanId(c1Id)).thenReturn(5L);
    when(clanMemberRepository.countByClanId(c2Id)).thenReturn(3L);
    when(activityProvider.getActivity(any())).thenReturn(new ClanActivitySnapshot(0.0, 1.0));
    when(modifierResolver.resolve(any())).thenReturn(new IdentityModifier());
    when(modifierResolver.describe(any())).thenReturn(ClanModifierStatus.none());

    ClanScoreData ranked1 = new ClanScoreData(c1Id, "Alpha", Tier.BRONZE, 100L, 5L);
    ClanScoreData ranked2 = new ClanScoreData(c2Id, "Beta", Tier.BRONZE, 50L, 3L);
    when(strategyFactory.getStrategy(Tier.BRONZE)).thenReturn(rankingStrategy);
    when(rankingStrategy.rank(anyList())).thenReturn(List.of(ranked1, ranked2));

    List<LeaderboardEntryResponse> result = leaderboardService.getLeaderboard(Tier.BRONZE);

    assertEquals(2, result.size());

    LeaderboardEntryResponse first = result.get(0);
    assertEquals(1, first.getRank());
    assertEquals(c1Id, first.getClanId());
    assertEquals("Alpha", first.getClanName());
    assertEquals(100L, first.getScore());
    assertEquals(Tier.BRONZE, first.getTier());

    LeaderboardEntryResponse second = result.get(1);
    assertEquals(2, second.getRank());
    assertEquals(c2Id, second.getClanId());
    assertEquals("Beta", second.getClanName());
    assertEquals(50L, second.getScore());
    assertEquals(Tier.BRONZE, second.getTier());
  }
}
