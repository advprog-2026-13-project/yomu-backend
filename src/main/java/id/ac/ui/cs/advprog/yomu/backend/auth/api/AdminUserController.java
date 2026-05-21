package id.ac.ui.cs.advprog.yomu.backend.auth.api;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

  private final UserRepository userRepository;

  public AdminUserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PutMapping("/{id}/promote")
  public ResponseEntity<Void> promoteToAdmin(@PathVariable UUID id) {
    var user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    user.setRole(Role.ADMIN);
    userRepository.save(user);

    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}/demote")
  public ResponseEntity<Void> demoteToUser(@PathVariable UUID id) {
    var user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    user.setRole(Role.USER);
    userRepository.save(user);

    return ResponseEntity.noContent().build();
  }
}
