package id.ac.ui.cs.advprog.yomu.backend.achievements.events.listener;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.service.AchievementProgressService;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementEnvelope;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementActivityPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AchievementActivityEventListener {

  private static final Logger logger =
      LoggerFactory.getLogger(AchievementActivityEventListener.class);

  private final AchievementProgressService achievementProgressService;

  public AchievementActivityEventListener(AchievementProgressService achievementProgressService) {
    this.achievementProgressService = achievementProgressService;
  }

  /**
   * Handles all achievement events generically via the {@link AchievementActivityPayload} contract.
   *
   * <p>Runs asynchronously on the {@code achievementTaskExecutor} thread pool so that the
   * publisher's thread (e.g., reading or quiz module) is not blocked.
   *
   * <p>{@code @Transactional} ensures that all database operations within the achievement progress
   * update are atomic, since async execution runs outside the publisher's transaction.
   *
   * <p>Adding a new achievement type requires ZERO changes here — just create a new payload class
   * that implements {@link AchievementActivityPayload} and publish it.
   */
  @Async("achievementTaskExecutor")
  @EventListener
  @Transactional
  public void handleAchievementEvent(AchievementEnvelope<?> envelope) {
    Object payload = envelope.getPayload();
    if (payload instanceof AchievementActivityPayload activityPayload) {
      logger.info(
          "Processing achievement event [type={}, userId={}] on thread {}",
          envelope.getAchievementType(),
          activityPayload.getUserId(),
          Thread.currentThread().getName());
      achievementProgressService.incrementProgress(
          activityPayload.getUserId(), envelope.getAchievementType(), 1);
    }
  }
}
