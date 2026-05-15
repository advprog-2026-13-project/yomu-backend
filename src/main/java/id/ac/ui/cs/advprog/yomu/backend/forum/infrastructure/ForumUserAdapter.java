package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.dto.UserSummary;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.UserPort;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ForumUserAdapter implements UserPort {

  private final UserRepository userRepository;

  public ForumUserAdapter(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Optional<UserSummary> findById(UUID id) {
    return userRepository.findById(id).map(this::toSummary);
  }

  @Override
  public List<UserSummary> findAllById(Collection<UUID> ids) {
    return userRepository.findAllById(ids).stream().map(this::toSummary).toList();
  }

  private UserSummary toSummary(User user) {
    return new UserSummary(user.getId(), user.getDisplayName());
  }
}
