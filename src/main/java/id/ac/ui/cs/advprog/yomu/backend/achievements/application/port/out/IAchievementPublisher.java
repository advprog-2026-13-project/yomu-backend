package id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementEnvelope;

public interface IAchievementPublisher {
  void publish(AchievementEnvelope<?> envelope);
}
