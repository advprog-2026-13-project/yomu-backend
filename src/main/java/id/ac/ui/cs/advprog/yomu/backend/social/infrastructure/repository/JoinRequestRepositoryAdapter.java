package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository;

import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.JoinRequestRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequest;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequestStatus;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.mapper.JoinRequestMapper;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.JoinRequestJpaEntity;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository.jpa.SpringDataJoinRequestRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JoinRequestRepositoryAdapter implements JoinRequestRepositoryPort {

  private final SpringDataJoinRequestRepository jpa;

  public JoinRequestRepositoryAdapter(SpringDataJoinRequestRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public JoinRequest save(JoinRequest request) {
    JoinRequestJpaEntity entity = JoinRequestMapper.toEntity(request);
    JoinRequestJpaEntity saved = jpa.save(entity);
    return JoinRequestMapper.toDomain(saved);
  }

  @Override
  public Optional<JoinRequest> findById(UUID id) {
    return jpa.findById(id).map(JoinRequestMapper::toDomain);
  }

  @Override
  public boolean existsByClanIdAndUserIdAndStatus(
      UUID clanId, UUID userId, JoinRequestStatus status) {
    return jpa.existsByClanIdAndUserIdAndStatus(clanId, userId, status);
  }

  @Override
  public List<JoinRequest> findByClanIdAndStatus(UUID clanId, JoinRequestStatus status) {
    return jpa.findByClanIdAndStatus(clanId, status).stream()
        .map(JoinRequestMapper::toDomain)
        .toList();
  }
}
