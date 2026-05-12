package id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;

public interface UserPort {
  Optional<User> findById(UUID id);

  List<User> findAllById(Collection<UUID> ids);
}
