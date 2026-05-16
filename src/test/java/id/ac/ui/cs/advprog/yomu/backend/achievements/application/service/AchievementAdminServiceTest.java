package id.ac.ui.cs.advprog.yomu.backend.achievements.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IAchievementRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IDailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import java.util.NoSuchElementException;
import java.util.Optional;
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
  void testUpdateAchievementSuccess() {
    UUID id = UUID.randomUUID();
    Achievement existing =
        new Achievement(id, "Old Name", "Old Desc", AchievementType.READING_COMPLETED, 10);
    Achievement updated =
        new Achievement(id, "New Name", "New Desc", AchievementType.READING_COMPLETED, 20);

    when(achievementRepository.findById(id)).thenReturn(Optional.of(existing));
    when(achievementRepository.save(any(Achievement.class))).thenReturn(updated);

    Achievement result = adminService.updateAchievement(id, "New Name", "New Desc", 20);

    assertNotNull(result);
    assertEquals("New Name", result.getName());
    assertEquals("New Desc", result.getDescription());
    assertEquals(20, result.getMilestone());
    verify(achievementRepository, times(1)).findById(id);
    verify(achievementRepository, times(1)).save(any(Achievement.class));
  }

  @Test
  void testUpdateAchievementNotFound() {
    UUID id = UUID.randomUUID();
    when(achievementRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(
        NoSuchElementException.class,
        () -> adminService.updateAchievement(id, "Name", "Desc", 10));

    verify(achievementRepository, times(1)).findById(id);
    verify(achievementRepository, never()).save(any(Achievement.class));
  }

  @Test
  void testUpdateAchievementPartialFields() {
    UUID id = UUID.randomUUID();
    Achievement existing =
        new Achievement(id, "Keep Name", "Keep Desc", AchievementType.READING_COMPLETED, 10);

    when(achievementRepository.findById(id)).thenReturn(Optional.of(existing));
    when(achievementRepository.save(any(Achievement.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Achievement result = adminService.updateAchievement(id, null, null, null);

    assertEquals("Keep Name", result.getName());
    assertEquals("Keep Desc", result.getDescription());
    assertEquals(10, result.getMilestone());
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
  void testUpdateDailyMissionSuccess() {
    UUID id = UUID.randomUUID();
    DailyMission existing =
        new DailyMission(id, "Old Mission", "Old Desc", AchievementType.QUIZ_COMPLETED, 5);
    DailyMission updated =
        new DailyMission(id, "New Mission", "New Desc", AchievementType.QUIZ_COMPLETED, 15);

    when(dailyMissionRepository.findById(id)).thenReturn(Optional.of(existing));
    when(dailyMissionRepository.save(any(DailyMission.class))).thenReturn(updated);

    DailyMission result = adminService.updateDailyMission(id, "New Mission", "New Desc", 15);

    assertNotNull(result);
    assertEquals("New Mission", result.getName());
    assertEquals("New Desc", result.getDescription());
    assertEquals(15, result.getMilestone());
    verify(dailyMissionRepository, times(1)).findById(id);
    verify(dailyMissionRepository, times(1)).save(any(DailyMission.class));
  }

  @Test
  void testUpdateDailyMissionNotFound() {
    UUID id = UUID.randomUUID();
    when(dailyMissionRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(
        NoSuchElementException.class,
        () -> adminService.updateDailyMission(id, "Name", "Desc", 10));

    verify(dailyMissionRepository, times(1)).findById(id);
    verify(dailyMissionRepository, never()).save(any(DailyMission.class));
  }

  @Test
  void testDeleteDailyMission() {
    UUID id = UUID.randomUUID();
    adminService.deleteDailyMission(id);
    verify(dailyMissionRepository, times(1)).deleteById(id);
  }
}

