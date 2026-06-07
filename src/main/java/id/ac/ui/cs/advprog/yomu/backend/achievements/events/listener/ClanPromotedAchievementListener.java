package id.ac.ui.cs.advprog.yomu.backend.achievements.events.listener;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementEnvelope;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.ClanReachedDiamondPayload;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.event.ClanPromotedEvent;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

// Bridges the clan-level ClanPromotedEvent to per-user achievement progress:
// publishes one AchievementEnvelope per member, handled by the generic async listener.
@Component
public class ClanPromotedAchievementListener {

  private final ApplicationEventPublisher eventPublisher;

  public ClanPromotedAchievementListener(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @EventListener
  public void onClanPromoted(ClanPromotedEvent event) {
    if (event.newTier() != Tier.DIAMOND) {
      return;
    }
    for (UUID userId : event.memberUserIds()) {
      eventPublisher.publishEvent(
          AchievementEnvelope.of(
              AchievementType.CLAN_REACHED_DIAMOND,
              1,
              new ClanReachedDiamondPayload(userId, event.clanId())));
    }
  }
}
