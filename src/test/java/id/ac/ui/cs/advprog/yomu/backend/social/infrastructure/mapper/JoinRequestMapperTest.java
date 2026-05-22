package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequest;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequestStatus;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.JoinRequestJpaEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JoinRequestMapperTest {

  @Test
  void toDomain_mapsAllFieldsCorrectly() {
    UUID id = UUID.randomUUID();
    UUID clanId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
    Instant resolvedAt = Instant.parse("2026-01-02T00:00:00Z");

    JoinRequestJpaEntity entity = new JoinRequestJpaEntity();
    entity.setId(id);
    entity.setClanId(clanId);
    entity.setUserId(userId);
    entity.setStatus(JoinRequestStatus.APPROVED);
    entity.setCreatedAt(createdAt);
    entity.setResolvedAt(resolvedAt);

    JoinRequest domain = JoinRequestMapper.toDomain(entity);

    assertEquals(id, domain.getId());
    assertEquals(clanId, domain.getClanId());
    assertEquals(userId, domain.getUserId());
    assertEquals(JoinRequestStatus.APPROVED, domain.getStatus());
    assertEquals(createdAt, domain.getCreatedAt());
    assertEquals(resolvedAt, domain.getResolvedAt());
  }

  @Test
  void toDomain_nullResolvedAt_remainsNull() {
    JoinRequestJpaEntity entity = new JoinRequestJpaEntity();
    entity.setId(UUID.randomUUID());
    entity.setClanId(UUID.randomUUID());
    entity.setUserId(UUID.randomUUID());
    entity.setStatus(JoinRequestStatus.PENDING);
    entity.setCreatedAt(Instant.now());
    entity.setResolvedAt(null);

    JoinRequest domain = JoinRequestMapper.toDomain(entity);

    assertNull(domain.getResolvedAt());
  }

  @Test
  void toEntity_mapsAllFieldsCorrectly() {
    JoinRequest domain = JoinRequest.create(UUID.randomUUID(), UUID.randomUUID());

    JoinRequestJpaEntity entity = JoinRequestMapper.toEntity(domain);

    assertEquals(domain.getId(), entity.getId());
    assertEquals(domain.getClanId(), entity.getClanId());
    assertEquals(domain.getUserId(), entity.getUserId());
    assertEquals(domain.getStatus(), entity.getStatus());
    assertEquals(domain.getCreatedAt(), entity.getCreatedAt());
    assertNull(entity.getResolvedAt());
  }

  @Test
  void toEntity_approvedRequest_preservesResolvedAt() {
    JoinRequest domain = JoinRequest.create(UUID.randomUUID(), UUID.randomUUID());
    domain.approve();

    JoinRequestJpaEntity entity = JoinRequestMapper.toEntity(domain);

    assertEquals(JoinRequestStatus.APPROVED, entity.getStatus());
    assertNotNull(entity.getResolvedAt());
  }
}
