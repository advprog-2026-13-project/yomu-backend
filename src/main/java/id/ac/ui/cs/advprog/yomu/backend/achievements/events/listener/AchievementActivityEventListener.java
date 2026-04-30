package id.ac.ui.cs.advprog.yomu.backend.achievements.events.listener;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.service.AchievementProgressService;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementEnvelope;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementQuizCompletedPayload;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementReadingCompletedPayload;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AchievementActivityEventListener {

    private final AchievementProgressService achievementProgressService;

    public AchievementActivityEventListener(AchievementProgressService achievementProgressService) {
        this.achievementProgressService = achievementProgressService;
    }

    @EventListener
    public void handleAchievementEvent(AchievementEnvelope<?> envelope) {
        UUID userId = null;

        Object payload = envelope.getPayload();
        if (payload instanceof AchievementReadingCompletedPayload) {
            userId = ((AchievementReadingCompletedPayload) payload).getUserId();
        } else if (payload instanceof AchievementQuizCompletedPayload) {
            userId = ((AchievementQuizCompletedPayload) payload).getUserId();
        }

        if (userId != null) {
            achievementProgressService.incrementProgress(userId, envelope.getAchievementType(), 1);
        }
    }
}
