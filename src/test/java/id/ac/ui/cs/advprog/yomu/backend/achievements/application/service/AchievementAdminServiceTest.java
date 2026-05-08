package id.ac.ui.cs.advprog.yomu.backend.achievements.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IAchievementRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IDailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AchievementAdminServiceTest {

  @Mock private IAchievementRepository achievementRepository;

  @Mock private IDailyMissionRepository dailyMissionRepository;

  @InjectMocks private AchievementAdminService adminService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateAchievement() {
    Achievement achievement =
        new Achievement(UUID.randomUUID(), "Test", "Desc", AchievementType.READING_COMPLETED, 10);
    when(achievementRepository.save(any(Achievement.class))).thenReturn(achievement);

    Achievement result =
        adminService.createAchievement("Test", "Desc", AchievementType.READING_COMPLETED, 10);

    assertNotNull(result);
    assertEquals("Test", result.getName());
    verify(achievementRepository, times(1)).save(any(Achievement.class));
  }

  @Test
  void testDeleteAchievement() {
    UUID id = UUID.randomUUID();
    adminService.deleteAchievement(id);
    verify(achievementRepository, times(1)).deleteById(id);
  }

  @Test
  void testCreateDailyMission() {
    DailyMission mission =
        new DailyMission(UUID.randomUUID(), "Mission", "Desc", AchievementType.QUIZ_COMPLETED, 5);
    when(dailyMissionRepository.save(any(DailyMission.class))).thenReturn(mission);

    DailyMission result =
        adminService.createDailyMission("Mission", "Desc", AchievementType.QUIZ_COMPLETED, 5);

    assertNotNull(result);
    assertEquals("Mission", result.getName());
    verify(dailyMissionRepository, times(1)).save(any(DailyMission.class));
  }

  @Test
  void testDeleteDailyMission() {
    UUID id = UUID.randomUUID();
    adminService.deleteDailyMission(id);
    verify(dailyMissionRepository, times(1)).deleteById(id);
  }
}
