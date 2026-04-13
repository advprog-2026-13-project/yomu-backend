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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class AchievementProgressService {

    private final IAchievementRepository achievementRepository;
    private final IUserAchievementProgressRepository userAchievementProgressRepository;
    private final IDailyMissionRepository dailyMissionRepository;
    private final IUserDailyMissionProgressRepository userDailyMissionProgressRepository;

    public AchievementProgressService(
            IAchievementRepository achievementRepository,
            IUserAchievementProgressRepository userAchievementProgressRepository,
            IDailyMissionRepository dailyMissionRepository,
            IUserDailyMissionProgressRepository userDailyMissionProgressRepository) {
        this.achievementRepository = achievementRepository;
        this.userAchievementProgressRepository = userAchievementProgressRepository;
        this.dailyMissionRepository = dailyMissionRepository;
        this.userDailyMissionProgressRepository = userDailyMissionProgressRepository;
    }

    /**
     * Increment progress for ALL achievements and daily missions of the given type.
     * This is the core method called by the event listener when an activity event
     * (e.g. READING_COMPLETED or QUIZ_COMPLETED) is received.
     *
     * @param userId the user who performed the activity
     * @param type   the achievement type that was triggered
     * @param amount the increment amount (usually 1)
     */
    public void incrementProgress(UUID userId, AchievementType type, int amount) {
        incrementAchievementProgress(userId, type, amount);
        incrementDailyMissionProgress(userId, type, amount);
    }

    private void incrementAchievementProgress(UUID userId, AchievementType type, int amount) {
        List<Achievement> achievements = achievementRepository.findByAchievementType(type);

        for (Achievement achievement : achievements) {
            UserAchievementProgress progress = userAchievementProgressRepository
                    .findByUserIdAndAchievementId(userId, achievement.getId())
                    .orElseGet(() -> new UserAchievementProgress(null, userId, achievement.getId()));

            boolean changed = progress.addProgress(amount, achievement.getMilestone());

            // Save only if addProgress actually changed the state (i.e. not already
            // completed)
            if (changed || !progress.isCompleted() || progress.getCurrentProgress() == amount) {
                userAchievementProgressRepository.save(progress);
            }
        }
    }

    private void incrementDailyMissionProgress(UUID userId, AchievementType type, int amount) {
        List<DailyMission> missions = dailyMissionRepository.findByTargetType(type);
        LocalDate today = LocalDate.now();

        for (DailyMission mission : missions) {
            UserDailyMissionProgress progress = userDailyMissionProgressRepository
                    .findByUserIdAndMissionIdAndDate(userId, mission.getId(), today)
                    .orElseGet(() -> new UserDailyMissionProgress(null, userId, mission.getId(), today));

            boolean changed = progress.addProgress(amount, mission.getMilestone());

            if (changed || !progress.isCompleted() || progress.getCurrentProgress() == amount) {
                userDailyMissionProgressRepository.save(progress);
            }
        }
    }
}
