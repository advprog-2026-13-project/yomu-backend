package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMemberRole;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequestStatus;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class JpaEntityOnCreateTest {

  @Test
  void clanJpaEntity_onCreate_setsCreatedAtWhenNull() {
    ClanJpaEntity entity = new ClanJpaEntity();
    entity.onCreate();
    assertNotNull(entity.getCreatedAt());
  }

  @Test
  void clanJpaEntity_onCreate_doesNotOverwriteExistingCreatedAt() {
    Instant fixed = Instant.parse("2026-01-01T00:00:00Z");
    ClanJpaEntity entity = new ClanJpaEntity();
    entity.setCreatedAt(fixed);
    entity.onCreate();
    assertEquals(fixed, entity.getCreatedAt());
  }

  @Test
  void clanJpaEntity_allArgsConstructor_andGetters() {
    Instant now = Instant.now();
    java.util.UUID id = java.util.UUID.randomUUID();
    java.util.UUID leaderId = java.util.UUID.randomUUID();
    ClanJpaEntity entity = new ClanJpaEntity(id, "Alpha", Tier.GOLD, 500L, leaderId, now);
    assertEquals(id, entity.getId());
    assertEquals("Alpha", entity.getName());
    assertEquals(Tier.GOLD, entity.getTier());
    assertEquals(500L, entity.getScore());
    assertEquals(leaderId, entity.getLeaderId());
    assertEquals(now, entity.getCreatedAt());
  }

  @Test
  void clanMemberJpaEntity_onCreate_setsJoinedAtWhenNull() {
    ClanMemberJpaEntity entity = new ClanMemberJpaEntity();
    entity.onCreate();
    assertNotNull(entity.getJoinedAt());
  }

  @Test
  void clanMemberJpaEntity_onCreate_doesNotOverwriteExistingJoinedAt() {
    Instant fixed = Instant.parse("2026-01-01T00:00:00Z");
    ClanMemberJpaEntity entity = new ClanMemberJpaEntity();
    entity.setJoinedAt(fixed);
    entity.onCreate();
    assertEquals(fixed, entity.getJoinedAt());
  }

  @Test
  void clanMemberJpaEntity_allArgsConstructor_andGetters() {
    Instant now = Instant.now();
    java.util.UUID id = java.util.UUID.randomUUID();
    java.util.UUID clanId = java.util.UUID.randomUUID();
    java.util.UUID userId = java.util.UUID.randomUUID();
    ClanMemberJpaEntity entity =
        new ClanMemberJpaEntity(id, clanId, userId, ClanMemberRole.MEMBER, now);
    assertEquals(id, entity.getId());
    assertEquals(clanId, entity.getClanId());
    assertEquals(userId, entity.getUserId());
    assertEquals(ClanMemberRole.MEMBER, entity.getRole());
    assertEquals(now, entity.getJoinedAt());
  }

  @Test
  void joinRequestJpaEntity_onCreate_setsCreatedAtWhenNull() {
    JoinRequestJpaEntity entity = new JoinRequestJpaEntity();
    entity.onCreate();
    assertNotNull(entity.getCreatedAt());
  }

  @Test
  void joinRequestJpaEntity_onCreate_doesNotOverwriteExistingCreatedAt() {
    Instant fixed = Instant.parse("2026-01-01T00:00:00Z");
    JoinRequestJpaEntity entity = new JoinRequestJpaEntity();
    entity.setCreatedAt(fixed);
    entity.onCreate();
    assertEquals(fixed, entity.getCreatedAt());
  }

  @Test
  void joinRequestJpaEntity_allArgsConstructor_andGetters() {
    Instant now = Instant.now();
    java.util.UUID id = java.util.UUID.randomUUID();
    java.util.UUID clanId = java.util.UUID.randomUUID();
    java.util.UUID userId = java.util.UUID.randomUUID();
    JoinRequestJpaEntity entity =
        new JoinRequestJpaEntity(id, clanId, userId, JoinRequestStatus.PENDING, now, null);
    assertEquals(id, entity.getId());
    assertEquals(clanId, entity.getClanId());
    assertEquals(userId, entity.getUserId());
    assertEquals(JoinRequestStatus.PENDING, entity.getStatus());
    assertEquals(now, entity.getCreatedAt());
  }

  @Test
  void leagueSeasonJpaEntity_gettersAndSetters() {
    java.time.LocalDateTime now = java.time.LocalDateTime.now();
    LeagueSeasonJpaEntity entity = new LeagueSeasonJpaEntity(1, now);
    assertEquals(1, entity.getId());
    assertEquals(now, entity.getStartedAt());

    java.time.LocalDateTime later = now.plusDays(1);
    entity.setStartedAt(later);
    assertEquals(later, entity.getStartedAt());
  }
}
