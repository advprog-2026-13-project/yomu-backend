package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.MeResponse;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.JwtService;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    String username = "rifqi";
    String displayName = "Rifqi Ilham";
    String email = "rifqi@mail.com";
    String phone = "08123";
    String password = "secret123";
    String encodedPassword = "encoded-password";

    RegisterRequest request = new RegisterRequest(username, displayName, email, phone, password);

    when(userRepository.existsByUsername(username)).thenReturn(false);
    when(userRepository.existsByEmail(email)).thenReturn(false);
    when(userRepository.existsByPhoneNumber(phone)).thenReturn(false);
    when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    MeResponse response = authService.register(request);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());

    User savedUser = userCaptor.getValue();
    assertEquals(username, savedUser.getUsername());
    assertEquals(displayName, savedUser.getDisplayName());
    assertEquals(email, savedUser.getEmail());
    assertEquals(phone, savedUser.getPhoneNumber());
    assertEquals(encodedPassword, savedUser.getPasswordHash());
    assertEquals(Role.USER, savedUser.getRole());

    assertEquals(username, response.getUsername());
    assertEquals(displayName, response.getDisplayName());
    assertEquals(Role.USER, response.getRole());
  }

  @Test
  void loginShouldSucceedUsingUsername() {
    LoginRequest request = new LoginRequest("rifqi", "secret123");
    User user =
        new User("rifqi", "Rifqi Ahmad", "rifqi@mail.com", "08123", "hashed-password", Role.USER);

    when(userRepository.findByEmail("rifqi")).thenReturn(Optional.empty()); // Login cek email dulu
    when(userRepository.findByUsername("rifqi")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("secret123", "hashed-password")).thenReturn(true);
    when(jwtService.generateToken(user)).thenReturn("jwt-token");

    AuthResponse response = authService.login(request);

    assertEquals("jwt-token", response.getAccessToken());
  }

  @Test
  void meShouldReturnCurrentAuthenticatedUser() {
    UUID id = UUID.randomUUID();
    User user =
        new User("rifqi", "Rifqi Ahmad", "rifqi@mail.com", "08123", "hashed-password", Role.USER);
    user.setId(id);

    SecurityUser principal = new SecurityUser(user);

    var authentication =
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    MeResponse response = authService.me();

    assertEquals("rifqi", response.getUsername());
    assertEquals("Rifqi Ahmad", response.getDisplayName());
    assertEquals("rifqi@mail.com", response.getEmail());
    assertEquals(Role.USER, response.getRole());
  }

  @Test
  void loginWithGoogleShouldCreateNewUserIfNotFound() {
    String idToken = "mock-google-token";
    var payload = mock(com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload.class);

    when(googleService.verifyToken(idToken)).thenReturn(payload);
    when(payload.getEmail()).thenReturn("google@mail.com");
    when(payload.getSubject()).thenReturn("123456789");
    when(payload.get("name")).thenReturn("Google User");

    when(userRepository.findByGoogleSub("123456789")).thenReturn(Optional.empty());
    when(userRepository.findByEmail("google@mail.com")).thenReturn(Optional.empty());

    authService.loginWithGoogle(idToken);

    verify(userRepository).save(any(User.class));
    verify(jwtService).generateToken(any(User.class));
  }
}
