package id.ac.ui.cs.advprog.yomu.backend.social.domain.event;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.List;
import java.util.UUID;

// memberUserIds carries all members at promotion time so consumers can act per-user
// without querying the Social repository.
// Plain POJO — Spring wraps non-ApplicationEvent objects, keeping domain free of Spring.
public record ClanPromotedEvent(
    UUID clanId, String clanName, Tier newTier, UUID leaderId, List<UUID> memberUserIds) {}
