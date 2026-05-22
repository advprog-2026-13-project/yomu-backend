package id.ac.ui.cs.advprog.yomu.backend.social.domain.strategy;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanScoreData;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BronzeRankingStrategyTest {

  private BronzeRankingStrategy strategy;

  @BeforeEach
  void setUp() {
    strategy = new BronzeRankingStrategy();
  }

  @Test
  void rank_multipleClans_sortsDescendingByTotalScore() {
    // Bronze: total score is the only metric — highest wins
    ClanScoreData low = score(100L);
    ClanScoreData mid = score(300L);
    ClanScoreData high = score(500L);

    List<ClanScoreData> result = strategy.rank(List.of(low, high, mid));

    assertSame(high, result.get(0));
    assertSame(mid, result.get(1));
    assertSame(low, result.get(2));
  }

  @Test
  void rank_twoClans_returnsHigherScorerFirst() {
    ClanScoreData lower = score(200L);
    ClanScoreData higher = score(201L);

    List<ClanScoreData> result = strategy.rank(List.of(lower, higher));

    assertSame(higher, result.get(0));
    assertSame(lower, result.get(1));
  }

  @Test
  void rank_emptyList_returnsEmptyList() {
    assertTrue(strategy.rank(List.of()).isEmpty());
  }

  @Test
  void rank_singleClan_returnsSingletonList() {
    ClanScoreData only = score(42L);
    List<ClanScoreData> result = strategy.rank(List.of(only));
    assertEquals(1, result.size());
    assertSame(only, result.get(0));
  }

  private ClanScoreData score(long score) {
    return new ClanScoreData(UUID.randomUUID(), "Clan", Tier.BRONZE, score, 5L);
  }
}
