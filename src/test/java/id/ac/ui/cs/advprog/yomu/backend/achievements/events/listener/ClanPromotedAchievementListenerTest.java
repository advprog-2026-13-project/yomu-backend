package id.ac.ui.cs.advprog.yomu.backend.achievements.events.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementEnvelope;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.ClanReachedDiamondPayload;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.event.ClanPromotedEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class ClanPromotedAchievementListenerTest {

  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private ClanPromotedAchievementListener listener;

  @Test
  void onClanPromoted_diamond_publishesOneEnvelopePerMember() {
    UUID clanId = UUID.randomUUID();
    UUID member1 = UUID.randomUUID();
    UUID member2 = UUID.randomUUID();
    ClanPromotedEvent event =
        new ClanPromotedEvent(clanId, "Vipers", Tier.DIAMOND, member1, List.of(member1, member2));

    listener.onClanPromoted(event);

    ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
    verify(eventPublisher, times(2)).publishEvent(captor.capture());

    List<UUID> publishedUserIds = new ArrayList<>();
    for (Object published : captor.getAllValues()) {
      AchievementEnvelope<?> envelope = (AchievementEnvelope<?>) published;
      assertEquals(AchievementType.CLAN_REACHED_DIAMOND, envelope.getAchievementType());
      ClanReachedDiamondPayload payload = (ClanReachedDiamondPayload) envelope.getPayload();
      assertEquals(clanId, payload.getClanId());
      publishedUserIds.add(payload.getUserId());
    }
    assertEquals(List.of(member1, member2), publishedUserIds);
  }

  @Test
  void onClanPromoted_nonDiamond_doesNotPublish() {
    UUID clanId = UUID.randomUUID();
    UUID member1 = UUID.randomUUID();
    ClanPromotedEvent event =
        new ClanPromotedEvent(clanId, "Vipers", Tier.GOLD, member1, List.of(member1));

    listener.onClanPromoted(event);

    verify(eventPublisher, never()).publishEvent(any());
  }
}
