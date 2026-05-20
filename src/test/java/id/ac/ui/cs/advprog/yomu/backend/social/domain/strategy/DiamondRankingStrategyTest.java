package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiamondRankingStrategyTest {

  private DiamondRankingStrategy strategy;

  @BeforeEach
  void setUp() {
    strategy = new DiamondRankingStrategy();
  }

  @Test
  void rank_normalClan_averageScorePerMember() {
    // 1000 / 5 = 200 → ranked by 200
    ClanScoreData clan = scoreData(1000L, 5L);
    List<ClanScoreData> result = strategy.rank(List.of(clan));
    assertEquals(1, result.size());
    assertSame(clan, result.get(0));
  }

  @Test
  void rank_exactlyThreeMembers_stillCountsAsValid() {
    // boundary: 3 anggota → average dihitung normal (3000/3 = 1000)
    ClanScoreData big = scoreData(3000L, 3L);
    ClanScoreData small = scoreData(100L, 10L); // avg 10, lebih rendah
    List<ClanScoreData> result = strategy.rank(List.of(small, big));
    // big harus rank 1 (avg 1000 > avg 10)
    assertSame(big, result.get(0));
    assertSame(small, result.get(1));
  }

  @Test
  void rank_twoMembers_scoreTreatedAsZero() {
    // < 3 anggota → skor efektif 0; clan lain yang valid harus lebih tinggi
    ClanScoreData below = scoreData(9999L, 2L);  // 2 anggota → 0
    ClanScoreData valid = scoreData(10L, 3L);    // 3 anggota → 10/3 ≈ 3.33
    List<ClanScoreData> result = strategy.rank(List.of(below, valid));
    assertSame(valid, result.get(0));
    assertSame(below, result.get(1));
  }

  @Test
  void rank_oneMember_scoreTreatedAsZero() {
    ClanScoreData below = scoreData(5000L, 1L);  // 1 anggota → 0
    ClanScoreData valid = scoreData(1L, 3L);     // 3 anggota → ~0.33, masih > 0
    List<ClanScoreData> result = strategy.rank(List.of(below, valid));
    assertSame(valid, result.get(0));
    assertSame(below, result.get(1));
  }

  @Test
  void rank_zeroMembers_scoreTreatedAsZero_noArithmeticException() {
    // defensive: memberCount == 0 → skor 0, tidak boleh throw ArithmeticException
    ClanScoreData zero = scoreData(1000L, 0L);
    assertDoesNotThrow(() -> strategy.rank(List.of(zero)));
    List<ClanScoreData> result = strategy.rank(List.of(zero));
    assertEquals(1, result.size());
  }

  private ClanScoreData scoreData(long score, long memberCount) {
    return new ClanScoreData(UUID.randomUUID(), "Clan", Tier.DIAMOND, score, memberCount);
  }
}
