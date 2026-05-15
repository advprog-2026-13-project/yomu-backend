package id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out;

import id.ac.ui.cs.advprog.yomu.backend.forum.application.dto.UserSummary;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPort {
  Optional<UserSummary> findById(UUID id);

  List<UserSummary> findAllById(Collection<UUID> ids);
}
