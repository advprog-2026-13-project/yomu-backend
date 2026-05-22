package id.ac.ui.cs.advprog.yomu.backend.achievements.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IAchievementRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IDailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IUserAchievementProgressRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IUserDailyMissionProgressRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserDailyMissionProgress;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AchievementQueryServiceTest {

  @Mock private IAchievementRepository achievementRepository;
  @Mock private IDailyMissionRepository dailyMissionRepository;
  @Mock private IUserAchievementProgressRepository userAchievementProgressRepository;
  @Mock private IUserDailyMissionProgressRepository userDailyMissionProgressRepository;

  @InjectMocks private AchievementQueryService queryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetAllAchievements() {
    when(achievementRepository.findAll()).thenReturn(List.of(mock(Achievement.class)));
    assertEquals(1, queryService.getAllAchievements().size());
  }

  @Test
  void testGetAllDailyMissions() {
    when(dailyMissionRepository.findAll()).thenReturn(List.of(mock(DailyMission.class)));
    assertEquals(1, queryService.getAllDailyMissions().size());
  }

  @Test
  void testGetUserAchievementProgress() {
    UUID userId = UUID.randomUUID();
    when(userAchievementProgressRepository.findByUserId(userId))
        .thenReturn(List.of(mock(UserAchievementProgress.class)));
    assertEquals(1, queryService.getUserAchievementProgress(userId).size());
  }

  @Test
  void testGetUserDailyMissionProgressForToday() {
    UUID userId = UUID.randomUUID();
    when(userDailyMissionProgressRepository.findByUserIdAndDate(userId, LocalDate.now()))
        .thenReturn(List.of(mock(UserDailyMissionProgress.class)));
    assertEquals(1, queryService.getUserDailyMissionProgressForToday(userId).size());
  }

  @Test
  void testGetCompletedAchievements() {
    UUID userId = UUID.randomUUID();
    when(userAchievementProgressRepository.findByUserIdAndIsCompleted(userId, true))
        .thenReturn(List.of(mock(UserAchievementProgress.class)));
    assertEquals(1, queryService.getCompletedAchievements(userId).size());
  }

  @Test
  void countMembersCompletedDailyMissionOn_returnsTwoWhenTwoOfThreeUsersCompleted() {
    List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    LocalDate date = LocalDate.of(2026, 5, 21);
    when(userDailyMissionProgressRepository.countDistinctCompletedUsersByUserIdInAndDate(
            userIds, date))
        .thenReturn(2L);

    assertEquals(2L, queryService.countMembersCompletedDailyMissionOn(userIds, date));
  }

  @Test
  void countMembersCompletedDailyMissionOn_returnsZeroWithoutCallingRepoWhenUserIdsEmpty() {
    assertEquals(0L, queryService.countMembersCompletedDailyMissionOn(List.of(), LocalDate.now()));
  }

  @Test
  void countMembersCompletedDailyMissionOn_returnsZeroWhenNoMembersCompleted() {
    List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    LocalDate date = LocalDate.of(2026, 5, 21);
    when(userDailyMissionProgressRepository.countDistinctCompletedUsersByUserIdInAndDate(
            userIds, date))
        .thenReturn(0L);

    assertEquals(0L, queryService.countMembersCompletedDailyMissionOn(userIds, date));
  }
}
