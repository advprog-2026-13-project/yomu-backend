package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.events.UserRegisteredEvent;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class GoogleAuthServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private JwtService jwtService;
  @Mock private GoogleService googleService;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private GoogleAuthService googleAuthService;

  private GoogleIdToken.Payload createPayload(String email, String sub, String name) {
    var payload = mock(GoogleIdToken.Payload.class);
    when(payload.getEmail()).thenReturn(email);
    when(payload.getSubject()).thenReturn(sub);
    when(payload.get("name")).thenReturn(name);
    when(payload.isEmpty()).thenReturn(false);
    return payload;
  }

  @Test
  void shouldThrowWhenEmailIsNull() {
    var payload = mock(GoogleIdToken.Payload.class);
    when(payload.isEmpty()).thenReturn(false);
    lenient().when(payload.getSubject()).thenReturn("sub123");
    lenient().when(payload.get("name")).thenReturn("Bob");
    when(googleService.verifyToken("token")).thenReturn(payload);

    assertThrows(IllegalArgumentException.class, () -> googleAuthService.loginWithGoogle("token"));
  }

  @Test
  void shouldThrowWhenSubjectIsNull() {
    var payload = mock(GoogleIdToken.Payload.class);
    when(payload.isEmpty()).thenReturn(false);
    lenient().when(payload.getEmail()).thenReturn("bob@mail.com");
    lenient().when(payload.get("name")).thenReturn("Bob");
    when(googleService.verifyToken("token")).thenReturn(payload);

    assertThrows(IllegalArgumentException.class, () -> googleAuthService.loginWithGoogle("token"));
  }

  @Test
  void shouldReturnTokenForExistingUserByGoogleSub() {
    var payload = createPayload("bob@mail.com", "sub123", "Bob");
    User user = new User("bob", "Bob", "bob@mail.com", null, "hash", Role.USER);
    user.setGoogleSub("sub123");
    when(googleService.verifyToken("token")).thenReturn(payload);
    when(userRepository.findByGoogleSub("sub123")).thenReturn(Optional.of(user));
    when(jwtService.generateToken(user)).thenReturn("jwt");

    var response = googleAuthService.loginWithGoogle("token");

    assertEquals("jwt", response.getAccessToken());
  }

  @Test
  void shouldLinkGoogleSubForExistingEmailAndReturnToken() {
    var payload = createPayload("bob@mail.com", "sub123", "Bob");
    User user = new User("bob", "Bob", "bob@mail.com", null, "hash", Role.USER);
    when(googleService.verifyToken("token")).thenReturn(payload);
    when(userRepository.findByGoogleSub("sub123")).thenReturn(Optional.empty());
    when(userRepository.findByEmail("bob@mail.com")).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    when(jwtService.generateToken(user)).thenReturn("jwt");

    var response = googleAuthService.loginWithGoogle("token");

    assertEquals("jwt", response.getAccessToken());
    assertEquals("sub123", user.getGoogleSub());
    verify(userRepository).save(user);
  }

  @Test
  void shouldCreateNewUserAndPublishEvent() {
    var payload = createPayload("new@mail.com", "sub999", "New User");
    when(googleService.verifyToken("token")).thenReturn(payload);
    when(userRepository.findByGoogleSub("sub999")).thenReturn(Optional.empty());
    when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
    when(jwtService.generateToken(any(User.class))).thenReturn("jwt");

    var response = googleAuthService.loginWithGoogle("token");

    assertEquals("jwt", response.getAccessToken());
    verify(eventPublisher).publishEvent(any(UserRegisteredEvent.class));
  }

  @Test
  void shouldUseDefaultDisplayNameWhenNameIsNull() {
    var payload = createPayload("new@mail.com", "sub999", null);
    when(googleService.verifyToken("token")).thenReturn(payload);
    when(userRepository.findByGoogleSub("sub999")).thenReturn(Optional.empty());
    when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
    when(jwtService.generateToken(any(User.class))).thenReturn("jwt");

    var response = googleAuthService.loginWithGoogle("token");

    assertNotNull(response);
    verify(userRepository).save(argThat(u -> u.getDisplayName().equals("Google User")));
  }

  @Test
  void shouldHandleUsernameCollision() {
    var payload = createPayload("collide@mail.com", "sub123456789", "Bob");
    when(googleService.verifyToken("token")).thenReturn(payload);
    when(userRepository.findByGoogleSub("sub123456789")).thenReturn(Optional.empty());
    when(userRepository.findByEmail("collide@mail.com")).thenReturn(Optional.empty());
    when(userRepository.existsByUsername(anyString())).thenReturn(true);
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
    when(jwtService.generateToken(any(User.class))).thenReturn("jwt");

    var response = googleAuthService.loginWithGoogle("token");

    assertNotNull(response);
    verify(userRepository).save(argThat(u -> u.getUsername().contains("collide")));
  }
}
