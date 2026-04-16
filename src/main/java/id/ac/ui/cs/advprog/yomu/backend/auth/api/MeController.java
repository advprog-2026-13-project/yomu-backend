package id.ac.ui.cs.advprog.yomu.backend.auth.api;

import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.MeResponse;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.UpdateAccountRequest; // Jangan lupa import ini
import id.ac.ui.cs.advprog.yomu.backend.auth.application.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
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
  public ResponseEntity<MeResponse> update(@RequestBody UpdateAccountRequest req) {
    return ResponseEntity.ok(authService.updateAccount(req));
  }

  @DeleteMapping
  public ResponseEntity<Void> delete() {
    authService.deleteAccount();
    return ResponseEntity.noContent().build(); 
  }
}