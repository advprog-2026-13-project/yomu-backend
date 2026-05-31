package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GoldRankingStrategyTest {

  private GoldRankingStrategy strategy;

  @BeforeEach
  void setUp() {
    strategy = new GoldRankingStrategy();
  }

  @Test
  void rank_normalClan_effectiveScoreIsHybridOfTotalAndAverage() {
    ClanScoreData clanA = scoreData(1000L, 5L);
    ClanScoreData clanB = scoreData(400L, 1L);
    List<ClanScoreData> result = strategy.rank(List.of(clanB, clanA));
    assertSame(clanA, result.get(0));
    assertSame(clanB, result.get(1));
  }

  @Test
  void rank_zeroMembers_effectiveScoreIsZeroNoArithmeticException() {
    ClanScoreData zero = scoreData(1000L, 0L);
    assertDoesNotThrow(() -> strategy.rank(List.of(zero)));
    List<ClanScoreData> result = strategy.rank(List.of(zero));
    assertEquals(1, result.size());
    assertSame(zero, result.get(0));
  }

  @Test
  void rank_oneMember_efficiencyEqualsTotalSoWeightDoubles() {
    ClanScoreData oneMember = scoreData(1000L, 1L);
    ClanScoreData twoMembers = scoreData(600L, 2L);
    List<ClanScoreData> result = strategy.rank(List.of(twoMembers, oneMember));
    assertSame(oneMember, result.get(0));
    assertSame(twoMembers, result.get(1));
  }

  @Test
  void rank_sameRawScore_goldOrdersDifferentlyFromBronzeAndDiamond() {
    ClanScoreData clanA = scoreData(900L, 2L);
    ClanScoreData clanB = scoreData(1000L, 20L);
    ClanScoreData clanC = scoreData(200L, 1L);
    List<ClanScoreData> result = strategy.rank(List.of(clanB, clanC, clanA));
    assertSame(clanA, result.get(0));
    assertSame(clanB, result.get(1));
    assertSame(clanC, result.get(2));
  }

  @Test
  void rank_fractionalAverage_doubleArithmeticPreservesOrderIntegerDivisionWouldNot() {
    ClanScoreData clanA = scoreData(5L, 3L);
    ClanScoreData clanB = scoreData(4L, 2L);
    List<ClanScoreData> result = strategy.rank(List.of(clanB, clanA));
    assertSame(clanA, result.get(0));
    assertSame(clanB, result.get(1));
  }

  @Test
  void rank_zeroMemberClanAmongNormalClans_scoredZeroAndRanksLast() {
    ClanScoreData zeroBig = scoreData(9999L, 0L);
    ClanScoreData small = scoreData(1L, 1L);
    List<ClanScoreData> result = strategy.rank(List.of(zeroBig, small));
    assertSame(small, result.get(0));
    assertSame(zeroBig, result.get(1));
  }

  private ClanScoreData scoreData(long score, long memberCount) {
    return new ClanScoreData(UUID.randomUUID(), "Clan", Tier.GOLD, score, memberCount);
  }
}
