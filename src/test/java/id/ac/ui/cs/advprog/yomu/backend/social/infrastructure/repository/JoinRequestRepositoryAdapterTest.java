package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequest;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequestStatus;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.JoinRequestJpaEntity;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository.jpa.SpringDataJoinRequestRepository;
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
class JoinRequestRepositoryAdapterTest {

  @Mock private SpringDataJoinRequestRepository jpa;
  @InjectMocks private JoinRequestRepositoryAdapter adapter;

  private JoinRequestJpaEntity makeEntity(UUID id, UUID clanId, UUID userId) {
    JoinRequestJpaEntity e = new JoinRequestJpaEntity();
    e.setId(id);
    e.setClanId(clanId);
    e.setUserId(userId);
    e.setStatus(JoinRequestStatus.PENDING);
    e.setCreatedAt(Instant.now());
    return e;
  }

  @Test
  void save_mapsAndReturnsDomain() {
    UUID id = UUID.randomUUID();
    UUID clanId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    JoinRequest req = new JoinRequest();
    req.setId(id);
    req.setClanId(clanId);
    req.setUserId(userId);
    req.setStatus(JoinRequestStatus.PENDING);

    JoinRequestJpaEntity saved = makeEntity(id, clanId, userId);
    when(jpa.save(any())).thenReturn(saved);

    JoinRequest result = adapter.save(req);
    assertEquals(id, result.getId());
    assertEquals(JoinRequestStatus.PENDING, result.getStatus());
    verify(jpa).save(any(JoinRequestJpaEntity.class));
  }

  @Test
  void findById_returnsMappedDomain_whenFound() {
    UUID id = UUID.randomUUID();
    UUID clanId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.of(makeEntity(id, clanId, userId)));
    Optional<JoinRequest> result = adapter.findById(id);
    assertTrue(result.isPresent());
    assertEquals(id, result.get().getId());
  }

  @Test
  void findById_returnsEmpty_whenNotFound() {
    UUID id = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.empty());
    assertTrue(adapter.findById(id).isEmpty());
  }

  @Test
  void existsByClanIdAndUserIdAndStatus_delegatesToJpa() {
    UUID clanId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    when(jpa.existsByClanIdAndUserIdAndStatus(clanId, userId, JoinRequestStatus.PENDING))
        .thenReturn(true);
    assertTrue(adapter.existsByClanIdAndUserIdAndStatus(clanId, userId, JoinRequestStatus.PENDING));
  }

  @Test
  void findByClanIdAndStatus_returnsMappedList() {
    UUID clanId = UUID.randomUUID();
    UUID id = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    when(jpa.findByClanIdAndStatus(clanId, JoinRequestStatus.PENDING))
        .thenReturn(List.of(makeEntity(id, clanId, userId)));
    List<JoinRequest> result = adapter.findByClanIdAndStatus(clanId, JoinRequestStatus.PENDING);
    assertEquals(1, result.size());
    assertEquals(clanId, result.get(0).getClanId());
  }

  @Test
  void findAll_returnsMappedList() {
    UUID id = UUID.randomUUID();
    UUID clanId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    when(jpa.findAll()).thenReturn(List.of(makeEntity(id, clanId, userId)));
    List<JoinRequest> result = adapter.findAll();
    assertEquals(1, result.size());
  }
}
