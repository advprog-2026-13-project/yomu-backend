package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.ClanJpaEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ClanMapperTest {

  @Test
  void toDomain_mapsAllFieldsCorrectly() {
    UUID id = UUID.randomUUID();
    UUID leaderId = UUID.randomUUID();
    Instant now = Instant.now();

    ClanJpaEntity entity = new ClanJpaEntity();
    entity.setId(id);
    entity.setName("Vipers");
    entity.setTier(Tier.GOLD);
    entity.setScore(500L);
    entity.setLeaderId(leaderId);
    entity.setCreatedAt(now);

    Clan clan = ClanMapper.toDomain(entity);

    assertEquals(id, clan.getId());
    assertEquals("Vipers", clan.getName());
    assertEquals(Tier.GOLD, clan.getTier());
    assertEquals(500L, clan.getScore());
    assertEquals(leaderId, clan.getLeaderId());
    assertEquals(now, clan.getCreatedAt());
  }

  @Test
  void toEntity_mapsAllFieldsCorrectly() {
    UUID id = UUID.randomUUID();
    UUID leaderId = UUID.randomUUID();
    Instant now = Instant.now();

    Clan clan = new Clan();
    clan.setId(id);
    clan.setName("Phoenix");
    clan.setTier(Tier.DIAMOND);
    clan.setScore(1200L);
    clan.setLeaderId(leaderId);
    clan.setCreatedAt(now);

    ClanJpaEntity entity = ClanMapper.toEntity(clan);

    assertEquals(id, entity.getId());
    assertEquals("Phoenix", entity.getName());
    assertEquals(Tier.DIAMOND, entity.getTier());
    assertEquals(1200L, entity.getScore());
    assertEquals(leaderId, entity.getLeaderId());
    assertEquals(now, entity.getCreatedAt());
  }

  @Test
  void toDomain_thenToEntity_roundTripPreservesAllFields() {
    UUID id = UUID.randomUUID();
    UUID leaderId = UUID.randomUUID();
    Instant now = Instant.now();

    ClanJpaEntity original = new ClanJpaEntity();
    original.setId(id);
    original.setName("Wolves");
    original.setTier(Tier.SILVER);
    original.setScore(750L);
    original.setLeaderId(leaderId);
    original.setCreatedAt(now);

    ClanJpaEntity roundTripped = ClanMapper.toEntity(ClanMapper.toDomain(original));

    assertEquals(original.getId(), roundTripped.getId());
    assertEquals(original.getName(), roundTripped.getName());
    assertEquals(original.getTier(), roundTripped.getTier());
    assertEquals(original.getScore(), roundTripped.getScore());
    assertEquals(original.getLeaderId(), roundTripped.getLeaderId());
    assertEquals(original.getCreatedAt(), roundTripped.getCreatedAt());
  }
}
