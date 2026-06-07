package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.ClanJpaEntity;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository.jpa.SpringDataClanRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClanRepositoryAdapterTest {

  @Mock private SpringDataClanRepository jpa;
  @InjectMocks private ClanRepositoryAdapter adapter;

  private ClanJpaEntity makeEntity(UUID id) {
    ClanJpaEntity e = new ClanJpaEntity();
    e.setId(id);
    e.setName("Vipers");
    e.setTier(Tier.BRONZE);
    e.setScore(0L);
    e.setLeaderId(UUID.randomUUID());
    e.setCreatedAt(Instant.now());
    return e;
  }

  @Test
  void existsByName_delegatesToJpa() {
    when(jpa.existsByName("Vipers")).thenReturn(true);
    assertTrue(adapter.existsByName("Vipers"));
  }

  @Test
  void findById_returnsMappedDomain_whenFound() {
    UUID id = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.of(makeEntity(id)));
    Optional<Clan> result = adapter.findById(id);
    assertTrue(result.isPresent());
    assertEquals(id, result.get().getId());
    assertEquals("Vipers", result.get().getName());
  }

  @Test
  void findById_returnsEmpty_whenNotFound() {
    UUID id = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.empty());
    assertTrue(adapter.findById(id).isEmpty());
  }

  @Test
  void save_mapsToEntitySavesAndReturnsDomain() {
    UUID id = UUID.randomUUID();
    Clan clan = new Clan();
    clan.setId(id);
    clan.setName("Vipers");
    clan.setTier(Tier.BRONZE);
    clan.setScore(0L);
    clan.setLeaderId(UUID.randomUUID());

    ClanJpaEntity saved = makeEntity(id);
    when(jpa.save(any())).thenReturn(saved);

    Clan result = adapter.save(clan);
    assertEquals(id, result.getId());
    verify(jpa).save(any(ClanJpaEntity.class));
  }

  @Test
  void deleteById_delegatesToJpa() {
    UUID id = UUID.randomUUID();
    adapter.deleteById(id);
    verify(jpa).deleteById(id);
  }

  @Test
  void findByTierOrderByScoreDesc_returnsEmptyList_whenNone() {
    when(jpa.findByTierOrderByScoreDesc(Tier.BRONZE)).thenReturn(List.of());
    assertTrue(adapter.findByTierOrderByScoreDesc(Tier.BRONZE).isEmpty());
  }

  @Test
  void findByTierOrderByScoreDesc_returnsMappedDomainList() {
    UUID id = UUID.randomUUID();
    when(jpa.findByTierOrderByScoreDesc(Tier.SILVER)).thenReturn(List.of(makeEntity(id)));
    List<Clan> result = adapter.findByTierOrderByScoreDesc(Tier.SILVER);
    assertEquals(1, result.size());
    assertEquals(id, result.get(0).getId());
  }

  @Test
  void findAll_returnsMappedDomainList() {
    UUID id = UUID.randomUUID();
    when(jpa.findAll()).thenReturn(List.of(makeEntity(id)));
    List<Clan> result = adapter.findAll();
    assertEquals(1, result.size());
    assertEquals(id, result.get(0).getId());
  }
}
