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

/**
 * Menjembatani {@link ClanPromotedEvent} (level-Clan, milik modul Social) ke model achievement yang
 * per-user. Saat Clan mencapai Tier tertinggi (Diamond), setiap anggota dianggap memperoleh
 * pencapaian {@code CLAN_REACHED_DIAMOND}.
 *
 * <p>Untuk tiap anggota diterbitkan satu {@link AchievementEnvelope}, lalu listener achievement
 * generik async ({@code AchievementActivityEventListener}) yang menaikkan progress — sehingga tidak
 * perlu mengubah listener generik itu (Open/Closed).
 */
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
