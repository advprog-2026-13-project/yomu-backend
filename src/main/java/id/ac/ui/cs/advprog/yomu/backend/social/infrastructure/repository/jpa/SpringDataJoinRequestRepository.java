package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository.jpa;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequestStatus;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.JoinRequestJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJoinRequestRepository
    extends JpaRepository<JoinRequestJpaEntity, UUID> {

  boolean existsByClanIdAndUserIdAndStatus(UUID clanId, UUID userId, JoinRequestStatus status);

  List<JoinRequestJpaEntity> findByClanIdAndStatus(UUID clanId, JoinRequestStatus status);
}
