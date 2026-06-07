package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SilverRankingStrategyTest {

  private SilverRankingStrategy strategy;

  @BeforeEach
  void setUp() {
    strategy = new SilverRankingStrategy();
  }

  @Test
  void rank_normalClan_effectiveScoreIsScorePlusMemberBonus() {
    ClanScoreData highMembers = scoreData(1000L, 5L);
    ClanScoreData lowMembers = scoreData(1040L, 0L);
    List<ClanScoreData> result = strategy.rank(List.of(lowMembers, highMembers));
    assertSame(highMembers, result.get(0));
    assertSame(lowMembers, result.get(1));
  }

  @Test
  void rank_zeroMembers_bonusIsZeroNoError() {
    ClanScoreData clan = scoreData(1000L, 0L);
    assertDoesNotThrow(() -> strategy.rank(List.of(clan)));
    List<ClanScoreData> result = strategy.rank(List.of(clan));
    assertEquals(1, result.size());
    assertSame(clan, result.get(0));
  }

  @Test
  void rank_twoClansDifferentScore_higherEffectiveScoreWins() {
    ClanScoreData stronger = scoreData(500L, 3L);
    ClanScoreData weaker = scoreData(300L, 5L);
    List<ClanScoreData> result = strategy.rank(List.of(weaker, stronger));
    assertSame(stronger, result.get(0));
    assertSame(weaker, result.get(1));
  }

  @Test
  void rank_sameTotalScoreDifferentMemberCount_memberBonusTiebreaks() {
    ClanScoreData moreMembersLowerScore = scoreData(900L, 10L);
    ClanScoreData fewerMembersHighScore = scoreData(990L, 1L);
    ClanScoreData clanA = scoreData(900L, 15L);
    ClanScoreData clanB = scoreData(1000L, 1L);
    List<ClanScoreData> result = strategy.rank(List.of(clanB, clanA));
    assertSame(clanA, result.get(0));
    assertSame(clanB, result.get(1));
  }

  private ClanScoreData scoreData(long score, long memberCount) {
    return new ClanScoreData(UUID.randomUUID(), "Clan", Tier.SILVER, score, memberCount);
  }
}
