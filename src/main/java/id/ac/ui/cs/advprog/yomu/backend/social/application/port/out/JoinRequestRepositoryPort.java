package id.ac.ui.cs.advprog.yomu.backend.social.application.port.out;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequest;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequestStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JoinRequestRepositoryPort {

  JoinRequest save(JoinRequest request);

  Optional<JoinRequest> findById(UUID id);

  boolean existsByClanIdAndUserIdAndStatus(UUID clanId, UUID userId, JoinRequestStatus status);

  List<JoinRequest> findByClanIdAndStatus(UUID clanId, JoinRequestStatus status);
}
