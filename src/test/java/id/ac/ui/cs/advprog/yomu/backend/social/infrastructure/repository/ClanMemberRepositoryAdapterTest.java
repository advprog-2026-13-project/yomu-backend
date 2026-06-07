package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMemberRole;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.ClanMemberJpaEntity;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository.jpa.SpringDataClanMemberRepository;
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
class ClanMemberRepositoryAdapterTest {

  @Mock private SpringDataClanMemberRepository jpa;
  @InjectMocks private ClanMemberRepositoryAdapter adapter;

  private ClanMemberJpaEntity makeEntity(UUID id, UUID clanId, UUID userId) {
    ClanMemberJpaEntity e = new ClanMemberJpaEntity();
    e.setId(id);
    e.setClanId(clanId);
    e.setUserId(userId);
    e.setRole(ClanMemberRole.MEMBER);
    e.setJoinedAt(Instant.now());
    return e;
  }

  @Test
  void findByUserId_returnsMappedDomain_whenFound() {
    UUID userId = UUID.randomUUID();
    UUID id = UUID.randomUUID();
    UUID clanId = UUID.randomUUID();
    when(jpa.findByUserId(userId)).thenReturn(Optional.of(makeEntity(id, clanId, userId)));
    Optional<ClanMember> result = adapter.findByUserId(userId);
    assertTrue(result.isPresent());
    assertEquals(userId, result.get().getUserId());
  }

  @Test
  void findByUserId_returnsEmpty_whenNotFound() {
    UUID userId = UUID.randomUUID();
    when(jpa.findByUserId(userId)).thenReturn(Optional.empty());
    assertTrue(adapter.findByUserId(userId).isEmpty());
  }

  @Test
  void findByClanId_returnsMappedList() {
    UUID clanId = UUID.randomUUID();
    UUID id = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    when(jpa.findByClanId(clanId)).thenReturn(List.of(makeEntity(id, clanId, userId)));
    List<ClanMember> result = adapter.findByClanId(clanId);
    assertEquals(1, result.size());
    assertEquals(clanId, result.get(0).getClanId());
  }

  @Test
  void existsByUserId_delegatesToJpa() {
    UUID userId = UUID.randomUUID();
    when(jpa.existsByUserId(userId)).thenReturn(true);
    assertTrue(adapter.existsByUserId(userId));
  }

  @Test
  void countByClanId_delegatesToJpa() {
    UUID clanId = UUID.randomUUID();
    when(jpa.countByClanId(clanId)).thenReturn(3L);
    assertEquals(3L, adapter.countByClanId(clanId));
  }

  @Test
  void save_mapsAndReturnsDomain() {
    UUID id = UUID.randomUUID();
    UUID clanId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    ClanMember member = new ClanMember();
    member.setId(id);
    member.setClanId(clanId);
    member.setUserId(userId);
    member.setRole(ClanMemberRole.MEMBER);

    ClanMemberJpaEntity saved = makeEntity(id, clanId, userId);
    when(jpa.save(any())).thenReturn(saved);

    ClanMember result = adapter.save(member);
    assertEquals(userId, result.getUserId());
    verify(jpa).save(any(ClanMemberJpaEntity.class));
  }

  @Test
  void delete_callsDeleteById() {
    UUID id = UUID.randomUUID();
    ClanMember member = new ClanMember();
    member.setId(id);
    adapter.delete(member);
    verify(jpa).deleteById(id);
  }

  @Test
  void deleteAll_mapsAndCallsJpaDeleteAll() {
    UUID id = UUID.randomUUID();
    UUID clanId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    ClanMember member = new ClanMember();
    member.setId(id);
    member.setClanId(clanId);
    member.setUserId(userId);
    member.setRole(ClanMemberRole.MEMBER);

    adapter.deleteAll(List.of(member));

    verify(jpa).deleteAll(any());
  }
}
