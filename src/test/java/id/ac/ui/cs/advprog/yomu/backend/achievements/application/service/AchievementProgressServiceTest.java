package id.ac.ui.cs.advprog.yomu.backend.achievements.application.service;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IAchievementRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IDailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IUserAchievementProgressRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IUserDailyMissionProgressRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserDailyMissionProgress;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementProgressServiceTest {

    @Mock
    private IAchievementRepository achievementRepository;

    @Mock
    private IUserAchievementProgressRepository userAchievementProgressRepository;

    @Mock
    private IDailyMissionRepository dailyMissionRepository;

    @Mock
    private IUserDailyMissionProgressRepository userDailyMissionProgressRepository;

    @InjectMocks
    private AchievementProgressService service;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    // ==========================================
    // Achievement Progress Tests
    // ==========================================

    @Test
    void testIncrementAchievementProgress_createsNewProgressIfNotExists() {
        Achievement achievement = new Achievement(
                UUID.randomUUID(), "Pembaca Cepat", "Baca 10 kali",
                AchievementType.READING_COMPLETED, 10);

        when(achievementRepository.findByAchievementType(AchievementType.READING_COMPLETED))
                .thenReturn(List.of(achievement));
        when(userAchievementProgressRepository.findByUserIdAndAchievementId(userId, achievement.getId()))
                .thenReturn(Optional.empty());
        when(userAchievementProgressRepository.save(any(UserAchievementProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.incrementProgress(userId, AchievementType.READING_COMPLETED, 1);

        ArgumentCaptor<UserAchievementProgress> captor = ArgumentCaptor.forClass(UserAchievementProgress.class);
        verify(userAchievementProgressRepository).save(captor.capture());

        UserAchievementProgress savedProgress = captor.getValue();
        assertEquals(userId, savedProgress.getUserId());
        assertEquals(achievement.getId(), savedProgress.getAchievementId());
        assertEquals(1, savedProgress.getCurrentProgress());
        assertFalse(savedProgress.isCompleted());
    }

    @Test
    void testIncrementAchievementProgress_updatesExistingProgress() {
        Achievement achievement = new Achievement(
                UUID.randomUUID(), "Pembaca Cepat", "Baca 10 kali",
                AchievementType.READING_COMPLETED, 10);
        UserAchievementProgress existingProgress =
                new UserAchievementProgress(UUID.randomUUID(), userId, achievement.getId());
        existingProgress.addProgress(3, 10); // already at 3

        when(achievementRepository.findByAchievementType(AchievementType.READING_COMPLETED))
                .thenReturn(List.of(achievement));
        when(userAchievementProgressRepository.findByUserIdAndAchievementId(userId, achievement.getId()))
                .thenReturn(Optional.of(existingProgress));
        when(userAchievementProgressRepository.save(any(UserAchievementProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.incrementProgress(userId, AchievementType.READING_COMPLETED, 1);

        ArgumentCaptor<UserAchievementProgress> captor = ArgumentCaptor.forClass(UserAchievementProgress.class);
        verify(userAchievementProgressRepository).save(captor.capture());

        assertEquals(4, captor.getValue().getCurrentProgress());
        assertFalse(captor.getValue().isCompleted());
    }

    @Test
    void testIncrementAchievementProgress_completesWhenMilestoneReached() {
        Achievement achievement = new Achievement(
                UUID.randomUUID(), "Pembaca Handal", "Baca 5 kali",
                AchievementType.READING_COMPLETED, 5);
        UserAchievementProgress existingProgress =
                new UserAchievementProgress(UUID.randomUUID(), userId, achievement.getId());
        existingProgress.addProgress(4, 5); // already at 4

        when(achievementRepository.findByAchievementType(AchievementType.READING_COMPLETED))
                .thenReturn(List.of(achievement));
        when(userAchievementProgressRepository.findByUserIdAndAchievementId(userId, achievement.getId()))
                .thenReturn(Optional.of(existingProgress));
        when(userAchievementProgressRepository.save(any(UserAchievementProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.incrementProgress(userId, AchievementType.READING_COMPLETED, 1);

        ArgumentCaptor<UserAchievementProgress> captor = ArgumentCaptor.forClass(UserAchievementProgress.class);
        verify(userAchievementProgressRepository).save(captor.capture());

        assertEquals(5, captor.getValue().getCurrentProgress());
        assertTrue(captor.getValue().isCompleted());
        assertNotNull(captor.getValue().getCompletedAt());
    }

    @Test
    void testIncrementAchievementProgress_handlesMultipleAchievementsOfSameType() {
        Achievement achievement1 = new Achievement(
                UUID.randomUUID(), "Pembaca Pemula", "Baca 5 kali",
                AchievementType.READING_COMPLETED, 5);
        Achievement achievement2 = new Achievement(
                UUID.randomUUID(), "Pembaca Handal", "Baca 50 kali",
                AchievementType.READING_COMPLETED, 50);

        when(achievementRepository.findByAchievementType(AchievementType.READING_COMPLETED))
                .thenReturn(List.of(achievement1, achievement2));
        when(userAchievementProgressRepository.findByUserIdAndAchievementId(eq(userId), any()))
                .thenReturn(Optional.empty());
        when(userAchievementProgressRepository.save(any(UserAchievementProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.incrementProgress(userId, AchievementType.READING_COMPLETED, 1);

        // Should save progress for BOTH achievements
        verify(userAchievementProgressRepository, times(2)).save(any(UserAchievementProgress.class));
    }

    @Test
    void testIncrementAchievementProgress_skipsAlreadyCompletedAchievement() {
        Achievement achievement = new Achievement(
                UUID.randomUUID(), "Pembaca Cepat", "Baca 5 kali",
                AchievementType.READING_COMPLETED, 5);
        UserAchievementProgress completedProgress =
                new UserAchievementProgress(UUID.randomUUID(), userId, achievement.getId());
        completedProgress.addProgress(5, 5); // completed

        when(achievementRepository.findByAchievementType(AchievementType.READING_COMPLETED))
                .thenReturn(List.of(achievement));
        when(userAchievementProgressRepository.findByUserIdAndAchievementId(userId, achievement.getId()))
                .thenReturn(Optional.of(completedProgress));

        service.incrementProgress(userId, AchievementType.READING_COMPLETED, 1);

        // addProgress returns false for completed -> should NOT save
        verify(userAchievementProgressRepository, never()).save(any());
    }

    @Test
    void testIncrementAchievementProgress_noAchievementsOfType() {
        when(achievementRepository.findByAchievementType(AchievementType.QUIZ_COMPLETED))
                .thenReturn(List.of());

        service.incrementProgress(userId, AchievementType.QUIZ_COMPLETED, 1);

        verify(userAchievementProgressRepository, never()).save(any());
    }

    // ==========================================
    // Daily Mission Progress Tests
    // ==========================================

    @Test
    void testIncrementDailyMissionProgress_createsNewProgressIfNotExists() {
        DailyMission mission = new DailyMission(
                UUID.randomUUID(), "Baca Harian", "Baca 3 kali hari ini",
                AchievementType.READING_COMPLETED, 3);

        when(dailyMissionRepository.findByTargetType(AchievementType.READING_COMPLETED))
                .thenReturn(List.of(mission));
        when(userDailyMissionProgressRepository.findByUserIdAndMissionIdAndDate(
                eq(userId), eq(mission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(userDailyMissionProgressRepository.save(any(UserDailyMissionProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.incrementProgress(userId, AchievementType.READING_COMPLETED, 1);

        ArgumentCaptor<UserDailyMissionProgress> captor =
                ArgumentCaptor.forClass(UserDailyMissionProgress.class);
        verify(userDailyMissionProgressRepository).save(captor.capture());

        UserDailyMissionProgress savedProgress = captor.getValue();
        assertEquals(userId, savedProgress.getUserId());
        assertEquals(mission.getId(), savedProgress.getMissionId());
        assertEquals(1, savedProgress.getCurrentProgress());
        assertFalse(savedProgress.isCompleted());
    }

    @Test
    void testIncrementDailyMissionProgress_updatesExistingProgress() {
        DailyMission mission = new DailyMission(
                UUID.randomUUID(), "Baca Harian", "Baca 3 kali hari ini",
                AchievementType.READING_COMPLETED, 3);
        UserDailyMissionProgress existingProgress =
                new UserDailyMissionProgress(UUID.randomUUID(), userId, mission.getId(), LocalDate.now());
        existingProgress.addProgress(1, 3); // at 1

        when(dailyMissionRepository.findByTargetType(AchievementType.READING_COMPLETED))
                .thenReturn(List.of(mission));
        when(userDailyMissionProgressRepository.findByUserIdAndMissionIdAndDate(
                eq(userId), eq(mission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.of(existingProgress));
        when(userDailyMissionProgressRepository.save(any(UserDailyMissionProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.incrementProgress(userId, AchievementType.READING_COMPLETED, 1);

        ArgumentCaptor<UserDailyMissionProgress> captor =
                ArgumentCaptor.forClass(UserDailyMissionProgress.class);
        verify(userDailyMissionProgressRepository).save(captor.capture());

        assertEquals(2, captor.getValue().getCurrentProgress());
        assertFalse(captor.getValue().isCompleted());
    }

    @Test
    void testIncrementDailyMissionProgress_completesWhenMilestoneReached() {
        DailyMission mission = new DailyMission(
                UUID.randomUUID(), "Baca Harian", "Baca 3 kali hari ini",
                AchievementType.READING_COMPLETED, 3);
        UserDailyMissionProgress existingProgress =
                new UserDailyMissionProgress(UUID.randomUUID(), userId, mission.getId(), LocalDate.now());
        existingProgress.addProgress(2, 3); // at 2

        when(dailyMissionRepository.findByTargetType(AchievementType.READING_COMPLETED))
                .thenReturn(List.of(mission));
        when(userDailyMissionProgressRepository.findByUserIdAndMissionIdAndDate(
                eq(userId), eq(mission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.of(existingProgress));
        when(userDailyMissionProgressRepository.save(any(UserDailyMissionProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.incrementProgress(userId, AchievementType.READING_COMPLETED, 1);

        ArgumentCaptor<UserDailyMissionProgress> captor =
                ArgumentCaptor.forClass(UserDailyMissionProgress.class);
        verify(userDailyMissionProgressRepository).save(captor.capture());

        assertEquals(3, captor.getValue().getCurrentProgress());
        assertTrue(captor.getValue().isCompleted());
        assertNotNull(captor.getValue().getCompletedAt());
    }

    @Test
    void testIncrementDailyMissionProgress_skipsAlreadyCompletedMission() {
        DailyMission mission = new DailyMission(
                UUID.randomUUID(), "Baca Harian", "Baca 3 kali hari ini",
                AchievementType.READING_COMPLETED, 3);
        UserDailyMissionProgress completedProgress =
                new UserDailyMissionProgress(UUID.randomUUID(), userId, mission.getId(), LocalDate.now());
        completedProgress.addProgress(3, 3); // already completed

        when(dailyMissionRepository.findByTargetType(AchievementType.READING_COMPLETED))
                .thenReturn(List.of(mission));
        when(userDailyMissionProgressRepository.findByUserIdAndMissionIdAndDate(
                eq(userId), eq(mission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.of(completedProgress));

        service.incrementProgress(userId, AchievementType.READING_COMPLETED, 1);

        // addProgress returns false for completed -> should NOT save
        verify(userDailyMissionProgressRepository, never()).save(any());
    }

    // ==========================================
    // Combined: Achievement + Daily Mission
    // ==========================================

    @Test
    void testIncrementProgress_updatesAchievementAndDailyMissionTogether() {
        Achievement achievement = new Achievement(
                UUID.randomUUID(), "Pembaca Cepat", "Baca 10 kali",
                AchievementType.READING_COMPLETED, 10);
        DailyMission mission = new DailyMission(
                UUID.randomUUID(), "Baca Harian", "Baca 3 kali hari ini",
                AchievementType.READING_COMPLETED, 3);

        when(achievementRepository.findByAchievementType(AchievementType.READING_COMPLETED))
                .thenReturn(List.of(achievement));
        when(dailyMissionRepository.findByTargetType(AchievementType.READING_COMPLETED))
                .thenReturn(List.of(mission));
        when(userAchievementProgressRepository.findByUserIdAndAchievementId(userId, achievement.getId()))
                .thenReturn(Optional.empty());
        when(userDailyMissionProgressRepository.findByUserIdAndMissionIdAndDate(
                eq(userId), eq(mission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(userAchievementProgressRepository.save(any(UserAchievementProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userDailyMissionProgressRepository.save(any(UserDailyMissionProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.incrementProgress(userId, AchievementType.READING_COMPLETED, 1);

        // Both repositories should have save called
        verify(userAchievementProgressRepository).save(any(UserAchievementProgress.class));
        verify(userDailyMissionProgressRepository).save(any(UserDailyMissionProgress.class));
    }
}
