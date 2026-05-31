package id.ac.ui.cs.advprog.yomu.backend.social.application.port.out;

import java.util.Optional;
import java.util.UUID;

public interface UserLookupPort {
  Optional<String> findUsernameById(UUID userId);
}
