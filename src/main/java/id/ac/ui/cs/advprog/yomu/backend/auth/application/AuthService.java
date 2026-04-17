package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.*;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.*;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final GoogleService googleService;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      GoogleService googleService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.googleService = googleService;
  }

  @Transactional
  public MeResponse register(RegisterRequest req) {
    if (userRepository.existsByUsername(req.getUsername()))
      throw new IllegalArgumentException("Username already taken");

    if ((req.getEmail() == null || req.getEmail().isBlank())
        && (req.getPhoneNumber() == null || req.getPhoneNumber().isBlank())) {
      throw new IllegalArgumentException("Email or Phone Number is required");
    }

    if (req.getEmail() != null && userRepository.existsByEmail(req.getEmail()))
      throw new IllegalArgumentException("Email already used");

    if (req.getPhoneNumber() != null && userRepository.existsByPhoneNumber(req.getPhoneNumber()))
      throw new IllegalArgumentException("Phone number already used");

    var user =
        new User(
            req.getUsername(),
            req.getDisplayName(),
            req.getEmail(),
            req.getPhoneNumber(),
            passwordEncoder.encode(req.getPassword()),
            Role.USER);

    user = userRepository.save(user);

    return mapToMeResponse(user);
  }

  public AuthResponse login(LoginRequest req) {
    String identifier = req.getIdentifier();

    var user =
        userRepository
            .findByEmail(identifier)
            .or(() -> userRepository.findByUsername(identifier))
            .or(() -> userRepository.findByPhoneNumber(identifier))
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

    if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
      throw new IllegalArgumentException("Invalid credentials");

    return new AuthResponse(jwtService.generateToken(user));
  }

  public MeResponse me() {
    return mapToMeResponse(getCurrentUser());
  }

  @Transactional
  public MeResponse updateAccount(UpdateAccountRequest req) {
    User user = getCurrentUser();

    if (req.getUsername() != null) user.setUsername(req.getUsername());
    if (req.getDisplayName() != null) user.setDisplayName(req.getDisplayName());
    if (req.getPassword() != null) user.setPasswordHash(passwordEncoder.encode(req.getPassword()));

    if (req.getEmail() != null) {
      if (!req.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(req.getEmail())) {
        throw new IllegalArgumentException("Email already used");
      }
      user.setEmail(req.getEmail());
    }

    if (req.getPhoneNumber() != null) {
      if (!req.getPhoneNumber().equals(user.getPhoneNumber())
          && userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
        throw new IllegalArgumentException("Phone number already used");
      }
      user.setPhoneNumber(req.getPhoneNumber());
    }

    userRepository.save(user);
    return mapToMeResponse(user);
  }

  @Transactional
  public void deleteAccount() {
    User user = getCurrentUser();
    userRepository.delete(user);
    SecurityContextHolder.clearContext();
  }

  private User getCurrentUser() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof SecurityUser principal))
      throw new IllegalStateException("Unauthenticated");

    return userRepository
        .findById(principal.getUser().getId())
        .orElseThrow(() -> new IllegalStateException("User not found"));
  }

  private MeResponse mapToMeResponse(User u) {
    return new MeResponse(
        u.getId(),
        u.getUsername(),
        u.getDisplayName(),
        u.getEmail(),
        u.getPhoneNumber(),
        u.getRole());
  }

  @Transactional
  public AuthResponse loginWithGoogle(String idToken) {
    var payload = googleService.verifyToken(idToken);
    if (payload == null) {
      throw new IllegalArgumentException("Invalid Google Token");
    }

    String email = payload.getEmail();
    String googleSub = payload.getSubject();
    String name = (String) payload.get("name");

    User user =
        userRepository
            .findByGoogleSub(googleSub)
            .orElseGet(() -> userRepository.findByEmail(email).orElse(null));

    if (user == null) {
      user =
          new User(
              email.split("@")[0] + "_" + googleSub.substring(0, 5),
              name,
              email,
              null,
              "",
              Role.USER);
      user.setGoogleSub(googleSub);
      userRepository.save(user);
    } else if (user.getGoogleSub() == null) {
      user.setGoogleSub(googleSub);
      userRepository.save(user);
    }

    return new AuthResponse(jwtService.generateToken(user));
  }
}
