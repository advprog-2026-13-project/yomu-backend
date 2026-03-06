package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IAchievementPublisher;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementEnvelope;
import org.springframework.context.ApplicationEventPublisher;

public class AchievementPublisher implements IAchievementPublisher {
  private final ApplicationEventPublisher publisher;

  public AchievementPublisher(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  @Override
  public void publish(AchievementEnvelope<?> envelope) {
    publisher.publishEvent(envelope);
  }
}
