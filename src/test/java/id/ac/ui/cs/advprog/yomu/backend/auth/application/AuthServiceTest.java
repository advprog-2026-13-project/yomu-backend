package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import static id.ac.ui.cs.advprog.yomu.backend.auth.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.*;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.*;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.JwtService;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;
  @Mock private GoogleService googleService;

  @InjectMocks private AuthService authService;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void registerShouldSucceedWhenUsernameAndEmailAreAvailable() {
    RegisterRequest request = createRegisterRequest();
    String encodedPassword = "encoded-password";

    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    MeResponse response = authService.register(request);

    assertNotNull(response);
    assertEquals(DEFAULT_USERNAME, response.getUsername());
    assertEquals(DEFAULT_DISPLAY_NAME, response.getDisplayName());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void loginShouldSucceedUsingUsername() {
    LoginRequest request = new LoginRequest(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    User user = createDummyUser();

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(userRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(jwtService.generateToken(user)).thenReturn("jwt-token");

    AuthResponse response = authService.login(request);

    assertNotNull(response);
    assertEquals("jwt-token", response.getAccessToken());
  }

  @Test
  void meShouldReturnCurrentAuthenticatedUser() {
    User user = createDummyUser();
    SecurityUser principal = new SecurityUser(user);

    var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

    MeResponse response = authService.me();

    assertNotNull(response);
    assertEquals(DEFAULT_USERNAME, response.getUsername());
    assertEquals(DEFAULT_DISPLAY_NAME, response.getDisplayName());
  }

  @Test
  void registerShouldThrowExceptionWhenUsernameTaken() {
    RegisterRequest request = createRegisterRequest();
    when(userRepository.existsByUsername(DEFAULT_USERNAME)).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    verify(userRepository, never()).save(any());
  }

  @Test
  void registerShouldFailIfEmailAlreadyExists() {
    RegisterRequest req = createRegisterRequest();
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> authService.register(req));
  }

  @Test
  void registerShouldFailIfPhoneAlreadyExists() {
    RegisterRequest req = createRegisterRequest();
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.existsByPhoneNumber(req.getPhoneNumber())).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> authService.register(req));
  }

  @Test
  void loginShouldFailWhenUserNotFound() {
    LoginRequest req = new LoginRequest("unknown", "password");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> authService.login(req));
  }

  @Test
  void loginShouldFailWhenPasswordIncorrect() {
    User user = createDummyUser();
    LoginRequest req = new LoginRequest(user.getUsername(), "wrong-password");

    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(eq("wrong-password"), anyString())).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> authService.login(req));
  }

  @Test
  void googleLoginShouldFailWhenTokenInvalid() {
    when(googleService.verifyToken("invalid-token")).thenReturn(null);
    assertThrows(
        IllegalArgumentException.class, () -> authService.loginWithGoogle("invalid-token"));
  }

  @Test
  void loginWithGoogleShouldReturnExistingUserBySub() {
    String idToken = "mock-token";
    var payload = mock(com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload.class);
    User existingUser = createDummyUser();

    when(googleService.verifyToken(idToken)).thenReturn(payload);
    when(payload.getSubject()).thenReturn("sub123");
    when(userRepository.findByGoogleSub("sub123")).thenReturn(Optional.of(existingUser));
    when(jwtService.generateToken(existingUser)).thenReturn("jwt");

    assertNotNull(authService.loginWithGoogle(idToken));
  }

  @Test
  void loginWithGoogleShouldLinkExistingEmailToGoogleSub() {
    String idToken = "mock-token";
    var payload = mock(com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload.class);
    User emailUser = createDummyUser();

    when(googleService.verifyToken(idToken)).thenReturn(payload);
    when(payload.getSubject()).thenReturn("sub123");
    when(payload.getEmail()).thenReturn(DEFAULT_EMAIL);

    when(userRepository.findByGoogleSub("sub123")).thenReturn(Optional.empty());
    when(userRepository.findByEmail(DEFAULT_EMAIL)).thenReturn(Optional.of(emailUser));

    authService.loginWithGoogle(idToken);

    verify(userRepository).save(emailUser);
  }

  @Test
  void updateAccountShouldSucceed() {
    User user = createDummyUser();
    // Mock user yang sedang login
    SecurityUser principal = new SecurityUser(user);
    var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    UpdateAccountRequest req = new UpdateAccountRequest();
    req.setDisplayName("New Name");
    req.setPhoneNumber("0999");

    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

    MeResponse response = authService.updateAccount(req);

    assertEquals("New Name", response.getDisplayName());
    assertEquals("0999", response.getPhoneNumber());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void deleteAccountShouldSucceed() {
    User user = createDummyUser();
    SecurityUser principal = new SecurityUser(user);
    var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    authService.deleteAccount();

    verify(userRepository).delete(user);
  }

  @Test
  void registerShouldThrowExceptionWhenEmailAlreadyExists() {
    RegisterRequest req = createRegisterRequest();
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> authService.register(req));
  }

  @Test
  void registerShouldThrowExceptionWhenPhoneAlreadyExists() {
    RegisterRequest req = createRegisterRequest();
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.existsByPhoneNumber(req.getPhoneNumber())).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> authService.register(req));
  }
}
