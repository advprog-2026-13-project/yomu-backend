package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// RankingStrategy.rank() returns List<ClanScoreData> sorted by effective score —
// it does NOT expose a numeric score. Tests verify ordering, not a raw number.
// Gold effective score = score*0.5 + (score/memberCount)*0.5 (double arithmetic).
class GoldRankingStrategyTest {

  private GoldRankingStrategy strategy;

  @BeforeEach
  void setUp() {
    strategy = new GoldRankingStrategy();
  }

  @Test
  void rank_normalClan_effectiveScoreIsHybridOfTotalAndAverage() {
    // clan A: score=1000, members=5  → 1000*0.5 + (1000/5)*0.5 = 500 + 100 = 600
    // clan B: score=400,  members=1  → 400*0.5  + (400/1)*0.5  = 200 + 200 = 400
    // clan A effective 600 > clan B effective 400 → A ranks first
    ClanScoreData clanA = scoreData(1000L, 5L);
    ClanScoreData clanB = scoreData(400L, 1L);
    List<ClanScoreData> result = strategy.rank(List.of(clanB, clanA));
    assertSame(clanA, result.get(0));
    assertSame(clanB, result.get(1));
  }

  @Test
  void rank_zeroMembers_effectiveScoreIsZeroNoArithmeticException() {
    // memberCount == 0 → skor 0 (defensive, tidak boleh throw ArithmeticException)
    ClanScoreData zero = scoreData(1000L, 0L);
    assertDoesNotThrow(() -> strategy.rank(List.of(zero)));
    List<ClanScoreData> result = strategy.rank(List.of(zero));
    assertEquals(1, result.size());
    assertSame(zero, result.get(0));
  }

  @Test
  void rank_oneMember_efficiencyEqualsTotalSoWeightDoubles() {
    // score=1000, members=1 → 1000*0.5 + (1000/1)*0.5 = 500 + 500 = 1000
    // score=600, members=2  → 600*0.5  + (600/2)*0.5  = 300 + 150 = 450
    // boundary: 1-member clan has effective score equal to its raw score
    ClanScoreData oneMember = scoreData(1000L, 1L);
    ClanScoreData twoMembers = scoreData(600L, 2L);
    List<ClanScoreData> result = strategy.rank(List.of(twoMembers, oneMember));
    assertSame(oneMember, result.get(0)); // 1000 > 450
    assertSame(twoMembers, result.get(1));
  }

  @Test
  void rank_sameRawScore_goldOrdersDifferentlyFromBronzeAndDiamond() {
    // Proves Gold is a distinct ordering from both Bronze (raw score) and Diamond (pure average).
    //
    // clan A: score=800, members=2  → Gold= 800*0.5 + 400*0.5 = 600
    //                                  Bronze= 800 (rank 2)
    //                                  Diamond= 400 (rank 2, assuming threshold met)
    // clan B: score=600, members=1  → Gold= 600*0.5 + 600*0.5 = 600  (tie → stable, B after A)
    // clan C: score=400, members=10 → Gold= 400*0.5 +  40*0.5 = 220
    //                                  Bronze= 400 (rank 3)
    //                                  Diamond=  40 (rank 3)
    //
    // Use a clearer non-tie scenario:
    // clan A: score=900, members=2  → Gold= 450 + 225 = 675
    // clan B: score=1000, members=20 → Gold= 500 + 25  = 525
    // clan C: score=200, members=1  → Gold= 100 + 100 = 200
    //
    // Bronze order: B(1000) > A(900) > C(200)
    // Diamond order (assuming all meet threshold): B(50) < A(450) ... wait
    //   Diamond: A=900/2=450, B=1000/20=50, C=200/1=200 → A > C > B
    // Gold order: A(675) > B(525) > C(200)
    // Gold order A>B>C differs from Bronze B>A>C and Diamond A>C>B — proves distinctness.
    ClanScoreData clanA = scoreData(900L, 2L);
    ClanScoreData clanB = scoreData(1000L, 20L);
    ClanScoreData clanC = scoreData(200L, 1L);
    List<ClanScoreData> result = strategy.rank(List.of(clanB, clanC, clanA));
    assertSame(clanA, result.get(0)); // Gold: 675
    assertSame(clanB, result.get(1)); // Gold: 525
    assertSame(clanC, result.get(2)); // Gold: 200
  }

  @Test
  void rank_fractionalAverage_doubleArithmeticPreservesOrderIntegerDivisionWouldNot() {
    // clan A: score=5, members=3 → effective = 2.5 + (5.0/3)*0.5 = 2.5 + 0.833 = 3.333
    // clan B: score=4, members=2 → effective = 2.0 + (4.0/2)*0.5 = 2.0 + 1.0   = 3.0
    //
    // With integer division (bug): floor(5/3)=1 → A_int=2.5+0.5=3.0, B_int=2.0+1.0=3.0 → TIE
    // Stable sort preserves input order → B stays first → WRONG order.
    //
    // With (double) cast (correct): A=3.333 > B=3.0 → A ranks first.
    ClanScoreData clanA = scoreData(5L, 3L);
    ClanScoreData clanB = scoreData(4L, 2L);
    // Input B before A so a tie (integer bug) would keep B first — exposing the bug.
    List<ClanScoreData> result = strategy.rank(List.of(clanB, clanA));
    assertSame(clanA, result.get(0));
    assertSame(clanB, result.get(1));
  }

  private ClanScoreData scoreData(long score, long memberCount) {
    return new ClanScoreData(UUID.randomUUID(), "Clan", Tier.GOLD, score, memberCount);
  }
}
