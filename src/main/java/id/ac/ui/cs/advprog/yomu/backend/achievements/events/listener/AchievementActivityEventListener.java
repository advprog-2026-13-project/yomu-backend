package id.ac.ui.cs.advprog.yomu.backend.achievements.events.listener;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.service.AchievementProgressService;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementEnvelope;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementActivityPayload;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AchievementActivityEventListener {

  private final AchievementProgressService achievementProgressService;

  public AchievementActivityEventListener(AchievementProgressService achievementProgressService) {
    this.achievementProgressService = achievementProgressService;
  }

  /**
   * Handles all achievement events generically via the {@link AchievementActivityPayload} contract.
   *
   * <p>Adding a new achievement type requires ZERO changes here — just create a new payload class
   * that implements {@link AchievementActivityPayload} and publish it.
   */
  @EventListener
  public void handleAchievementEvent(AchievementEnvelope<?> envelope) {
    Object payload = envelope.getPayload();
    if (payload instanceof AchievementActivityPayload activityPayload) {
      achievementProgressService.incrementProgress(
          activityPayload.getUserId(), envelope.getAchievementType(), 1);
    }
  }
}
