package id.ac.ui.cs.advprog.yomu.backend.social.application;

import static org.mockito.Mockito.verify;

import id.ac.ui.cs.advprog.yomu.backend.shared.event.QuizCompletedEvent;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClanScoreEventListenerTest {

  @Mock private ClanService clanService;

  @InjectMocks private ClanScoreEventListener listener;

  @Test
  void handleQuizCompleted_delegatesToAddScoreToMemberClan() {
    UUID userId = UUID.randomUUID();
    UUID readingId = UUID.randomUUID();
    QuizCompletedEvent event = new QuizCompletedEvent(this, userId, readingId, 8, 10, 150L);

    listener.handleQuizCompleted(event);

    verify(clanService).addScoreToMemberClan(userId, 150L);
  }
}
