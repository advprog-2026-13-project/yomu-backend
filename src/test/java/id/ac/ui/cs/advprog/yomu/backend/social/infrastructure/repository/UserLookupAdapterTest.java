package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserLookupAdapterTest {

  @Mock private UserRepository userRepository;
  @InjectMocks private UserLookupAdapter adapter;

  @Test
  void findUsernameById_returnsUsername_whenUserExists() {
    UUID userId = UUID.randomUUID();
    User user = new User("fatta", "Fatta", "fatta@mail.com", "0800", "hashed", Role.USER);
    user.setId(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    Optional<String> result = adapter.findUsernameById(userId);

    assertTrue(result.isPresent());
    assertEquals("fatta", result.get());
  }

  @Test
  void findUsernameById_returnsEmpty_whenUserNotFound() {
    UUID userId = UUID.randomUUID();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    Optional<String> result = adapter.findUsernameById(userId);

    assertTrue(result.isEmpty());
  }
}
