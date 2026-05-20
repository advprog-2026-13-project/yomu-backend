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
    // 1000 + (5 × 10) = 1050
    ClanScoreData highMembers = scoreData(1000L, 5L);
    ClanScoreData lowMembers = scoreData(1040L, 0L); // 1040 + 0 = 1040
    // highMembers effective 1050 > lowMembers effective 1040 → highMembers rank 1
    List<ClanScoreData> result = strategy.rank(List.of(lowMembers, highMembers));
    assertSame(highMembers, result.get(0));
    assertSame(lowMembers, result.get(1));
  }

  @Test
  void rank_zeroMembers_bonusIsZeroNoError() {
    // score + (0 × 10) = score; must not throw
    ClanScoreData clan = scoreData(1000L, 0L);
    assertDoesNotThrow(() -> strategy.rank(List.of(clan)));
    List<ClanScoreData> result = strategy.rank(List.of(clan));
    assertEquals(1, result.size());
    assertSame(clan, result.get(0));
  }

  @Test
  void rank_twoClansDifferentScore_higherEffectiveScoreWins() {
    ClanScoreData stronger = scoreData(500L, 3L); // 500 + 30 = 530
    ClanScoreData weaker = scoreData(300L, 5L); // 300 + 50 = 350
    List<ClanScoreData> result = strategy.rank(List.of(weaker, stronger));
    assertSame(stronger, result.get(0));
    assertSame(weaker, result.get(1));
  }

  @Test
  void rank_sameTotalScoreDifferentMemberCount_memberBonusTiebreaks() {
    // Proves Silver differs from Bronze: Bronze would treat these as equal,
    // Silver correctly separates them via the member bonus.
    ClanScoreData moreMembersLowerScore = scoreData(900L, 10L); // 900 + 100 = 1000
    ClanScoreData fewerMembersHighScore = scoreData(990L, 1L); // 990 +  10 = 1000... tie?
    // Let's make the separation clearer:
    // clan A: score=900, members=15 → 900 + 150 = 1050
    // clan B: score=1000, members=1 → 1000 + 10 = 1010
    ClanScoreData clanA = scoreData(900L, 15L); // effective 1050
    ClanScoreData clanB = scoreData(1000L, 1L); // effective 1010
    // Bronze would rank clanB first (raw score 1000 > 900), Silver ranks clanA first
    List<ClanScoreData> result = strategy.rank(List.of(clanB, clanA));
    assertSame(clanA, result.get(0)); // Silver flips Bronze ordering
    assertSame(clanB, result.get(1));
  }

  private ClanScoreData scoreData(long score, long memberCount) {
    return new ClanScoreData(UUID.randomUUID(), "Clan", Tier.SILVER, score, memberCount);
  }
}
