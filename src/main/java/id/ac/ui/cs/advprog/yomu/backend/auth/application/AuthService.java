package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.*;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.*;
import id.ac.ui.cs.advprog.yomu.backend.auth.events.UserRegisteredEvent;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.*;
import org.springframework.context.ApplicationEventPublisher;
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
  private final ApplicationEventPublisher eventPublisher;
  private final LoginRateLimiter rateLimiter;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      GoogleService googleService,
      ApplicationEventPublisher eventPublisher,
      LoginRateLimiter rateLimiter) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.googleService = googleService;
    this.eventPublisher = eventPublisher;
    this.rateLimiter = rateLimiter;
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

    var saved = userRepository.save(user);

    eventPublisher.publishEvent(
        new UserRegisteredEvent(saved.getId(), saved.getUsername(), saved.getDisplayName()));

    return MeResponse.fromEntity(saved);
  }

  public AuthResponse login(LoginRequest req) {
    String identifier = req.getIdentifier();

    if (rateLimiter.isBlocked(identifier)) {
      throw new IllegalArgumentException(
          "Account temporarily locked due to too many failed attempts");
    }

    var user =
        userRepository
            .findByEmail(identifier)
            .or(() -> userRepository.findByUsername(identifier))
            .or(() -> userRepository.findByPhoneNumber(identifier))
            .orElseThrow(
                () -> {
                  rateLimiter.recordFailure(identifier);
                  return new IllegalArgumentException("Invalid credentials");
                });

    if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
      rateLimiter.recordFailure(identifier);
      throw new IllegalArgumentException("Invalid credentials");
    }

    rateLimiter.reset(identifier);
    return new AuthResponse(jwtService.generateToken(user));
  }

  public MeResponse me() {
    return MeResponse.fromEntity(getCurrentUser());
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

    return MeResponse.fromEntity(userRepository.save(user));
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

  @Transactional
  public AuthResponse loginWithGoogle(String idToken) {
    var payload = googleService.verifyToken(idToken);
    if (payload.isEmpty()) throw new IllegalArgumentException("Invalid Google Token");

    String email = payload.getEmail();
    String googleSub = payload.getSubject();

    if (email == null || googleSub == null) {
      throw new IllegalArgumentException("Google account data is incomplete");
    }

    Object nameObj = payload.get("name");
    String name = (nameObj != null) ? nameObj.toString() : "Google User";

    User user =
        userRepository
            .findByGoogleSub(googleSub)
            .or(() -> userRepository.findByEmail(email))
            .map(
                existingUser -> {
                  if (existingUser.getGoogleSub() == null) {
                    existingUser.setGoogleSub(googleSub);
                    return userRepository.save(existingUser);
                  }
                  return existingUser;
                })
            .orElseGet(
                () -> {
                  String subSuffix = googleSub.length() > 5 ? googleSub.substring(0, 5) : googleSub;
                  User newUser =
                      new User(
                          email.split("@")[0] + "_" + subSuffix, name, email, null, "", Role.USER);
                  newUser.setGoogleSub(googleSub);
                  return userRepository.save(newUser);
                });

    return new AuthResponse(jwtService.generateToken(user));
  }
}
