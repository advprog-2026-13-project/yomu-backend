package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure;

import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementEnvelope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AchievementPublisherTest {

  @Test
  void shouldDelegateToSpringApplicationEventPublisher() {
    ApplicationEventPublisher springPublisher = mock(ApplicationEventPublisher.class);
    AchievementPublisher publisher = new AchievementPublisher(springPublisher);

    AchievementEnvelope<?> envelope = mock(AchievementEnvelope.class);
    publisher.publish(envelope);

    verify(springPublisher).publishEvent(envelope);
    verifyNoMoreInteractions(springPublisher);
  }
}
