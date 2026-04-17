package id.ac.ui.cs.advprog.yomu.backend.social.application;

import id.ac.ui.cs.advprog.yomu.backend.shared.event.QuizCompletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ClanScoreEventListener {

  private final ClanService clanService;

  public ClanScoreEventListener(ClanService clanService) {
    this.clanService = clanService;
  }

  @EventListener
  public void handleQuizCompleted(QuizCompletedEvent event) {
    clanService.addScoreToMemberClan(event.getUserId(), event.getScoreEarned());
  }
}