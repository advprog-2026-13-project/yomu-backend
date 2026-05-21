package id.ac.ui.cs.advprog.yomu.backend.auth.api;

import id.ac.ui.cs.advprog.yomu.backend.auth.application.AuthService;
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

  private final AuthService authService;

  public AdminUserController(AuthService authService) {
    this.authService = authService;
  }

  @PutMapping("/{id}/promote")
  public ResponseEntity<Void> promoteToAdmin(@PathVariable UUID id) {
    authService.promoteToAdmin(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}/demote")
  public ResponseEntity<Void> demoteToUser(@PathVariable UUID id) {
    authService.demoteToUser(id);
    return ResponseEntity.noContent().build();
  }
}
