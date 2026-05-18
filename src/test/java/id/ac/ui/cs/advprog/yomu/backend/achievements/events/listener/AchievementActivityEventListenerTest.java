package id.ac.ui.cs.advprog.yomu.backend.achievements.events.listener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.service.AchievementProgressService;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementEnvelope;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementActivityPayload;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementQuizCompletedPayload;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementReadingCompletedPayload;
import java.lang.reflect.Method;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

class AchievementActivityEventListenerTest {

  @Mock private AchievementProgressService achievementProgressService;

  @InjectMocks private AchievementActivityEventListener listener;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testHandleReadingCompletedEvent() {
    UUID userId = UUID.randomUUID();
    AchievementReadingCompletedPayload payload =
        new AchievementReadingCompletedPayload(userId, UUID.randomUUID(), 120);
    AchievementEnvelope<AchievementReadingCompletedPayload> envelope =
        AchievementEnvelope.of(AchievementType.READING_COMPLETED, 1, payload);

    listener.handleAchievementEvent(envelope);

    verify(achievementProgressService, times(1))
        .incrementProgress(userId, AchievementType.READING_COMPLETED, 1);
  }

  @Test
  void testHandleQuizCompletedEvent() {
    UUID userId = UUID.randomUUID();
    AchievementQuizCompletedPayload payload =
        new AchievementQuizCompletedPayload(userId, UUID.randomUUID(), 100, true);
    AchievementEnvelope<AchievementQuizCompletedPayload> envelope =
        AchievementEnvelope.of(AchievementType.QUIZ_COMPLETED, 1, payload);

    listener.handleAchievementEvent(envelope);

    verify(achievementProgressService, times(1))
        .incrementProgress(userId, AchievementType.QUIZ_COMPLETED, 1);
  }

  @Test
  void testHandleUnknownPayload() {
    Object unknownPayload = new Object();
    AchievementEnvelope<Object> envelope =
        AchievementEnvelope.of(AchievementType.READING_COMPLETED, 1, unknownPayload);

    listener.handleAchievementEvent(envelope);

    verify(achievementProgressService, never()).incrementProgress(any(), any(), anyInt());
  }

  @Test
  void testHandleAnyPayloadImplementingInterface() {
    // Simulates a future payload type — the listener should handle it
    // without any code changes, proving OCP compliance.
    UUID userId = UUID.randomUUID();
    AchievementActivityPayload futurePayload = () -> userId;

    AchievementEnvelope<AchievementActivityPayload> envelope =
        AchievementEnvelope.of(AchievementType.READING_COMPLETED, 1, futurePayload);

    listener.handleAchievementEvent(envelope);

    verify(achievementProgressService, times(1))
        .incrementProgress(userId, AchievementType.READING_COMPLETED, 1);
  }

  @Test
  void handlerMethodShouldBeAnnotatedWithAsync() throws NoSuchMethodException {
    Method method =
        AchievementActivityEventListener.class.getMethod(
            "handleAchievementEvent", AchievementEnvelope.class);

    Async asyncAnnotation = method.getAnnotation(Async.class);
    assertNotNull(asyncAnnotation, "handleAchievementEvent should be annotated with @Async");
    assertEquals(
        "achievementTaskExecutor",
        asyncAnnotation.value(),
        "Should use the dedicated achievementTaskExecutor");
  }

  @Test
  void handlerMethodShouldBeAnnotatedWithTransactional() throws NoSuchMethodException {
    Method method =
        AchievementActivityEventListener.class.getMethod(
            "handleAchievementEvent", AchievementEnvelope.class);

    Transactional txAnnotation = method.getAnnotation(Transactional.class);
    assertNotNull(txAnnotation, "handleAchievementEvent should be annotated with @Transactional");
  }
}
