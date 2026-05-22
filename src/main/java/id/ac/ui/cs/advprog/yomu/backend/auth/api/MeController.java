package id.ac.ui.cs.advprog.yomu.backend.auth.api;

import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.MeResponse;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.UpdateAccountRequest;
import id.ac.ui.cs.advprog.yomu.backend.auth.application.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/me")
public class MeController {
  private final AuthService authService;

  public MeController(AuthService authService) {
    this.authService = authService;
  }

  @GetMapping
  public ResponseEntity<MeResponse> me() {
    return ResponseEntity.ok(authService.me());
  }

  @PatchMapping
  public ResponseEntity<MeResponse> update(@Valid @RequestBody UpdateAccountRequest req) {
    return ResponseEntity.ok(authService.updateAccount(req));
  }

  @DeleteMapping
  public ResponseEntity<Void> delete() {
    authService.deleteAccount();
    return ResponseEntity.noContent().build();
  }
}
