package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.service.AchievementQueryService;
import id.ac.ui.cs.advprog.yomu.backend.reading.application.QuizStatsQueryService;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ClanActivitySnapshot;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RealClanActivityProviderTest {

  @Mock private ClanMemberRepositoryPort clanMemberRepository;
  @Mock private AchievementQueryService achievementQueryService;
  @Mock private QuizStatsQueryService quizStatsQueryService;

  private RealClanActivityProvider provider;

  @BeforeEach
  void setUp() {
    provider =
        new RealClanActivityProvider(
            clanMemberRepository, achievementQueryService, quizStatsQueryService);
  }

  @Test
  void getActivity_computesRateAndAccuracyFromMemberData() {
    UUID clanId = UUID.randomUUID();
    UUID u1 = UUID.randomUUID();
    UUID u2 = UUID.randomUUID();
    UUID u3 = UUID.randomUUID();

    ClanMember m1 = new ClanMember();
    m1.setUserId(u1);
    ClanMember m2 = new ClanMember();
    m2.setUserId(u2);
    ClanMember m3 = new ClanMember();
    m3.setUserId(u3);

    when(clanMemberRepository.findByClanId(clanId)).thenReturn(List.of(m1, m2, m3));

    List<UUID> userIds = List.of(u1, u2, u3);
    when(achievementQueryService.countMembersCompletedDailyMissionOn(userIds, LocalDate.now()))
        .thenReturn(2L);
    when(quizStatsQueryService.averageScore(userIds)).thenReturn(0.75);

    ClanActivitySnapshot snapshot = provider.getActivity(clanId);

    assertEquals(2.0 / 3.0, snapshot.dailyMissionCompletionRate(), 0.001);
    assertEquals(0.75, snapshot.averageAccuracy(), 0.001);
  }

  @Test
  void getActivity_returnsZeroRateAndFullAccuracyWhenClanHasNoMembers() {
    UUID clanId = UUID.randomUUID();
    when(clanMemberRepository.findByClanId(clanId)).thenReturn(List.of());

    ClanActivitySnapshot snapshot = provider.getActivity(clanId);

    assertEquals(0.0, snapshot.dailyMissionCompletionRate(), 0.001);
    assertEquals(1.0, snapshot.averageAccuracy(), 0.001);
  }

  @Test
  void getActivity_returnsFullRateWhenAllMembersCompleted() {
    UUID clanId = UUID.randomUUID();
    UUID u1 = UUID.randomUUID();
    UUID u2 = UUID.randomUUID();

    ClanMember m1 = new ClanMember();
    m1.setUserId(u1);
    ClanMember m2 = new ClanMember();
    m2.setUserId(u2);

    when(clanMemberRepository.findByClanId(clanId)).thenReturn(List.of(m1, m2));

    List<UUID> userIds = List.of(u1, u2);
    when(achievementQueryService.countMembersCompletedDailyMissionOn(userIds, LocalDate.now()))
        .thenReturn(2L);
    when(quizStatsQueryService.averageScore(userIds)).thenReturn(0.9);

    ClanActivitySnapshot snapshot = provider.getActivity(clanId);

    assertEquals(1.0, snapshot.dailyMissionCompletionRate(), 0.001);
    assertEquals(0.9, snapshot.averageAccuracy(), 0.001);
  }
}
