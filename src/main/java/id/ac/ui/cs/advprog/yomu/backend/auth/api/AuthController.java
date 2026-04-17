package id.ac.ui.cs.advprog.yomu.backend.auth.api;

import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.*;
import id.ac.ui.cs.advprog.yomu.backend.auth.application.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<MeResponse> register(@Valid @RequestBody RegisterRequest req) {
    return ResponseEntity.ok(authService.register(req));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
    return ResponseEntity.ok(authService.login(req));
  }

  @PostMapping("/google")
  public ResponseEntity<AuthResponse> googleLogin(@RequestBody Map<String, String> request) {
    String token = request.get("token");

    if (token == null || token.isBlank()) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(authService.loginWithGoogle(token));
  }
}
