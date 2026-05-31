package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository;

import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.UserLookupPort;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserLookupAdapter implements UserLookupPort {

  private final UserRepository userRepository;

  public UserLookupAdapter(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Optional<String> findUsernameById(UUID userId) {
    return userRepository.findById(userId).map(u -> u.getUsername());
  }
}
