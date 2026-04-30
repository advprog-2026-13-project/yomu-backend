package id.ac.ui.cs.advprog.yomu.backend.achievements.application.service;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IAchievementRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IDailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AchievementAdminService {

    private final IAchievementRepository achievementRepository;
    private final IDailyMissionRepository dailyMissionRepository;

    public AchievementAdminService(IAchievementRepository achievementRepository, IDailyMissionRepository dailyMissionRepository) {
        this.achievementRepository = achievementRepository;
        this.dailyMissionRepository = dailyMissionRepository;
    }

    public Achievement createAchievement(String name, String description, AchievementType type, int milestone) {
        Achievement achievement = new Achievement(null, name, description, type, milestone);
        return achievementRepository.save(achievement);
    }

    public void deleteAchievement(UUID id) {
        achievementRepository.deleteById(id);
    }

    public DailyMission createDailyMission(String name, String description, AchievementType targetType, int milestone) {
        DailyMission mission = new DailyMission(null, name, description, targetType, milestone);
        return dailyMissionRepository.save(mission);
    }

    public void deleteDailyMission(UUID id) {
        dailyMissionRepository.deleteById(id);
    }
}
