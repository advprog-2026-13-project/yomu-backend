package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMemberRole;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.ClanMemberJpaEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ClanMemberMapperTest {

  @Test
  void toDomain_mapsAllFieldsCorrectly() {
    UUID id = UUID.randomUUID();
    UUID clanId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    Instant joinedAt = Instant.now();

    ClanMemberJpaEntity entity = new ClanMemberJpaEntity();
    entity.setId(id);
    entity.setClanId(clanId);
    entity.setUserId(userId);
    entity.setRole(ClanMemberRole.LEADER);
    entity.setJoinedAt(joinedAt);

    ClanMember member = ClanMemberMapper.toDomain(entity);

    assertEquals(id, member.getId());
    assertEquals(clanId, member.getClanId());
    assertEquals(userId, member.getUserId());
    assertEquals(ClanMemberRole.LEADER, member.getRole());
    assertEquals(joinedAt, member.getJoinedAt());
  }

  @Test
  void toEntity_mapsAllFieldsCorrectly() {
    UUID id = UUID.randomUUID();
    UUID clanId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    Instant joinedAt = Instant.now();

    ClanMember member = new ClanMember();
    member.setId(id);
    member.setClanId(clanId);
    member.setUserId(userId);
    member.setRole(ClanMemberRole.MEMBER);
    member.setJoinedAt(joinedAt);

    ClanMemberJpaEntity entity = ClanMemberMapper.toEntity(member);

    assertEquals(id, entity.getId());
    assertEquals(clanId, entity.getClanId());
    assertEquals(userId, entity.getUserId());
    assertEquals(ClanMemberRole.MEMBER, entity.getRole());
    assertEquals(joinedAt, entity.getJoinedAt());
  }

  @Test
  void toDomain_thenToEntity_roundTripPreservesAllFields() {
    UUID id = UUID.randomUUID();
    UUID clanId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    Instant joinedAt = Instant.now();

    ClanMemberJpaEntity original = new ClanMemberJpaEntity();
    original.setId(id);
    original.setClanId(clanId);
    original.setUserId(userId);
    original.setRole(ClanMemberRole.MEMBER);
    original.setJoinedAt(joinedAt);

    ClanMemberJpaEntity roundTripped =
        ClanMemberMapper.toEntity(ClanMemberMapper.toDomain(original));

    assertEquals(original.getId(), roundTripped.getId());
    assertEquals(original.getClanId(), roundTripped.getClanId());
    assertEquals(original.getUserId(), roundTripped.getUserId());
    assertEquals(original.getRole(), roundTripped.getRole());
    assertEquals(original.getJoinedAt(), roundTripped.getJoinedAt());
  }
}
