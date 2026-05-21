package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.events.UserRegisteredEvent;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoogleAuthService {

  private static final Logger log = LoggerFactory.getLogger(GoogleAuthService.class);

  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final GoogleService googleService;
  private final ApplicationEventPublisher eventPublisher;

  public GoogleAuthService(
      UserRepository userRepository,
      JwtService jwtService,
      GoogleService googleService,
      ApplicationEventPublisher eventPublisher) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.googleService = googleService;
    this.eventPublisher = eventPublisher;
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
    String displayName = (nameObj != null) ? nameObj.toString() : "Google User";

    var existingBySub = userRepository.findByGoogleSub(googleSub);
    if (existingBySub.isPresent()) {
      return new AuthResponse(jwtService.generateToken(existingBySub.get()));
    }

    var existingByEmail = userRepository.findByEmail(email);
    if (existingByEmail.isPresent()) {
      var user = existingByEmail.get();
      user.setGoogleSub(googleSub);
      userRepository.save(user);
      return new AuthResponse(jwtService.generateToken(user));
    }

    String username = generateUniqueUsername(email, googleSub);

    var newUser = new User(username, displayName, email, null, "", Role.USER);
    newUser.setGoogleSub(googleSub);
    var saved = userRepository.save(newUser);

    eventPublisher.publishEvent(
        new UserRegisteredEvent(saved.getId(), saved.getUsername(), saved.getDisplayName()));

    return new AuthResponse(jwtService.generateToken(saved));
  }

  private String generateUniqueUsername(String email, String googleSub) {
    String base = email.split("@")[0];
    String suffix = googleSub.length() > 5 ? googleSub.substring(0, 5) : googleSub;
    String candidate = base + "_" + suffix;

    if (userRepository.existsByUsername(candidate)) {
      candidate = base + "_" + googleSub.substring(0, 8);
    }

    return candidate;
  }
}
