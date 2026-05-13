package id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import id.ac.ui.cs.advprog.yomu.backend.forum.application.dto.UserSummary;

public interface UserPort {
  Optional<UserSummary> findById(UUID id);

  List<UserSummary> findAllById(Collection<UUID> ids);
}
