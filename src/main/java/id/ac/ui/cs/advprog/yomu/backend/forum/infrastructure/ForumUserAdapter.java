package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.UserPort;

@Component
public class ForumUserAdapter implements UserPort {

  private final UserRepository userRepository;

  public ForumUserAdapter(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Optional<User> findById(UUID id) {
    return userRepository.findById(id);
  }

  @Override
  public List<User> findAllById(Collection<UUID> ids) {
    return userRepository.findAllById(ids);
  }
}
