package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.*;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.*;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(
      UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public MeResponse register(RegisterRequest req) {
    if (userRepository.existsByUsername(req.getUsername()))
      throw new IllegalArgumentException("Username already taken");
    if (userRepository.existsByEmail(req.getEmail()))
      throw new IllegalArgumentException("Email already used");

    var user =
        new User(
            req.getUsername(),
            req.getEmail(),
            passwordEncoder.encode(req.getPassword()),
            Role.USER);
    userRepository.save(user);

    return new MeResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
  }

  public AuthResponse login(LoginRequest req) {
    var userOpt =
        req.getIdentifier().contains("@")
            ? userRepository.findByEmail(req.getIdentifier())
            : userRepository.findByUsername(req.getIdentifier());

    var user = userOpt.orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

    if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
      throw new IllegalArgumentException("Invalid credentials");

    return new AuthResponse(jwtService.generateToken(user));
  }

  public MeResponse me() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof SecurityUser principal))
      throw new IllegalStateException("Unauthenticated");

    var u = principal.getUser();
    return new MeResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole());
  }
}
