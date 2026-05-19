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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;
  @Mock private GoogleService googleService;
  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private LoginRateLimiter rateLimiter;

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

    when(rateLimiter.isBlocked(anyString())).thenReturn(false);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(userRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(jwtService.generateToken(user)).thenReturn("jwt-token");

    AuthResponse response = authService.login(request);

    assertNotNull(response);
    assertEquals("jwt-token", response.getAccessToken());
    verify(rateLimiter).reset(DEFAULT_USERNAME);
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
    when(rateLimiter.isBlocked("unknown")).thenReturn(false);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> authService.login(req));
    verify(rateLimiter).recordFailure("unknown");
  }

  @Test
  void loginShouldFailWhenPasswordIncorrect() {
    User user = createDummyUser();
    LoginRequest req = new LoginRequest(user.getUsername(), "wrong-password");

    when(rateLimiter.isBlocked(anyString())).thenReturn(false);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(eq("wrong-password"), anyString())).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> authService.login(req));
    verify(rateLimiter).recordFailure(user.getUsername());
  }

  @Test
  void loginShouldFailWhenRateLimited() {
    LoginRequest req = new LoginRequest("blockeduser", "password");
    when(rateLimiter.isBlocked("blockeduser")).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> authService.login(req));
    verify(userRepository, never()).findByEmail(anyString());
  }

  @Test
  void googleLoginShouldFailWhenTokenInvalid() {
    when(googleService.verifyToken("invalid-token"))
        .thenReturn(new com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload());
    assertThrows(
        IllegalArgumentException.class, () -> authService.loginWithGoogle("invalid-token"));
  }

  @Test
  void loginWithGoogleShouldReturnExistingUserBySub() {
    String idToken = "mock-token";
    var payload = mock(com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload.class);
    User existingUser = createDummyUser();
    existingUser.setGoogleSub("sub123");

    when(googleService.verifyToken(idToken)).thenReturn(payload);
    when(payload.getSubject()).thenReturn("sub123");

    when(payload.getEmail()).thenReturn(DEFAULT_EMAIL);

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
  void registerShouldFailWhenNoEmailAndNoPhone() {
    RegisterRequest req = new RegisterRequest("user", "User", null, null, "password");
    assertThrows(IllegalArgumentException.class, () -> authService.register(req));
  }

  @Test
  void loginShouldSucceedUsingPhoneNumber() {
    LoginRequest request = new LoginRequest(DEFAULT_PHONE, DEFAULT_PASSWORD);
    User user = createDummyUser();

    when(rateLimiter.isBlocked(anyString())).thenReturn(false);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
    when(userRepository.findByPhoneNumber(DEFAULT_PHONE)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(jwtService.generateToken(user)).thenReturn("jwt-token");

    AuthResponse response = authService.login(request);

    assertNotNull(response);
    assertEquals("jwt-token", response.getAccessToken());
  }

  @Test
  void updateAccountShouldUpdateUsername() {
    User user = createDummyUser();
    SecurityUser principal = new SecurityUser(user);
    var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    UpdateAccountRequest req = new UpdateAccountRequest();
    req.setUsername("newusername");

    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

    MeResponse response = authService.updateAccount(req);

    assertEquals("newusername", response.getUsername());
  }

  @Test
  void updateAccountShouldUpdatePassword() {
    User user = createDummyUser();
    SecurityUser principal = new SecurityUser(user);
    var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    UpdateAccountRequest req = new UpdateAccountRequest();
    req.setPassword("newpassword");

    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(passwordEncoder.encode("newpassword")).thenReturn("encoded-new");
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

    MeResponse response = authService.updateAccount(req);

    assertNotNull(response);
    verify(passwordEncoder).encode("newpassword");
  }

  @Test
  void loginWithGoogleShouldThrowWhenEmailIsNull() {
    String idToken = "mock-token";
    var payload = mock(com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload.class);

    when(googleService.verifyToken(idToken)).thenReturn(payload);
    when(payload.getSubject()).thenReturn("sub123");
    when(payload.getEmail()).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> authService.loginWithGoogle(idToken));
  }

  @Test
  void loginWithGoogleShouldCreateNewUser() {
    String idToken = "mock-token";
    var payload = mock(com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload.class);

    when(googleService.verifyToken(idToken)).thenReturn(payload);
    when(payload.getSubject()).thenReturn("sub123-new");
    when(payload.getEmail()).thenReturn("newuser@gmail.com");
    when(payload.get("name")).thenReturn("New User");

    when(userRepository.findByGoogleSub("sub123-new")).thenReturn(Optional.empty());
    when(userRepository.findByEmail("newuser@gmail.com")).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
    when(jwtService.generateToken(any(User.class))).thenReturn("jwt");

    assertNotNull(authService.loginWithGoogle(idToken));
  }

  @Test
  void meShouldThrowWhenNotAuthenticated() {
    assertThrows(IllegalStateException.class, () -> authService.me());
  }

  @Test
  void deleteAccountShouldThrowWhenNotAuthenticated() {
    assertThrows(IllegalStateException.class, () -> authService.deleteAccount());
  }
}
