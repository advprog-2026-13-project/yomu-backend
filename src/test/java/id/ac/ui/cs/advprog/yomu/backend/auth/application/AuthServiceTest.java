package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

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
        RegisterRequest request = new RegisterRequest("rifqi", "rifqi@mail.com", "secret123");

        when(userRepository.existsByUsername("rifqi")).thenReturn(false);
        when(userRepository.existsByEmail("rifqi@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");

        MeResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("rifqi", savedUser.getUsername());
        assertEquals("rifqi@mail.com", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPasswordHash());
        assertEquals(Role.USER, savedUser.getRole());

        assertEquals("rifqi", response.getUsername());
        assertEquals("rifqi@mail.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
    }

    @Test
    void registerShouldFailWhenUsernameAlreadyTaken() {
        RegisterRequest request = new RegisterRequest("rifqi", "rifqi@mail.com", "secret123");

        when(userRepository.existsByUsername("rifqi")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals("Username already taken", exception.getMessage());
        verify(userRepository, never()).existsByEmail(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerShouldFailWhenEmailAlreadyUsed() {
        RegisterRequest request = new RegisterRequest("rifqi", "rifqi@mail.com", "secret123");

        when(userRepository.existsByUsername("rifqi")).thenReturn(false);
        when(userRepository.existsByEmail("rifqi@mail.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals("Email already used", exception.getMessage());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginShouldSucceedUsingUsername() {
        LoginRequest request = new LoginRequest("rifqi", "secret123");
        User user = new User("rifqi", "rifqi@mail.com", "hashed-password", Role.USER);

        when(userRepository.findByUsername("rifqi")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("jwt-token", response.getAccessToken());
        verify(userRepository).findByUsername("rifqi");
        verify(userRepository, never()).findByEmail(any());
        verify(passwordEncoder).matches("secret123", "hashed-password");
        verify(jwtService).generateToken(user);
    }

    @Test
    void loginShouldSucceedUsingEmail() {
        LoginRequest request = new LoginRequest("rifqi@mail.com", "secret123");
        User user = new User("rifqi", "rifqi@mail.com", "hashed-password", Role.USER);

        when(userRepository.findByEmail("rifqi@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("jwt-token", response.getAccessToken());
        verify(userRepository).findByEmail("rifqi@mail.com");
        verify(userRepository, never()).findByUsername(any());
        verify(passwordEncoder).matches("secret123", "hashed-password");
        verify(jwtService).generateToken(user);
    }

    @Test
    void loginShouldFailWhenUserNotFound() {
        LoginRequest request = new LoginRequest("rifqi", "secret123");

        when(userRepository.findByUsername("rifqi")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid credentials", exception.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void loginShouldFailWhenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest("rifqi", "wrong-password");
        User user = new User("rifqi", "rifqi@mail.com", "hashed-password", Role.USER);

        when(userRepository.findByUsername("rifqi")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid credentials", exception.getMessage());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void meShouldReturnCurrentAuthenticatedUser() {
        User user = new User("rifqi", "rifqi@mail.com", "hashed-password", Role.USER);
        SecurityUser principal = new SecurityUser(user);

        var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MeResponse response = authService.me();

        assertEquals("rifqi", response.getUsername());
        assertEquals("rifqi@mail.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
    }

    @Test
    void meShouldFailWhenAuthenticationIsNull() {
        SecurityContextHolder.clearContext();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> authService.me()
        );

        assertEquals("Unauthenticated", exception.getMessage());
    }

    @Test
    void meShouldFailWhenPrincipalIsNotSecurityUser() {
        var authentication = new UsernamePasswordAuthenticationToken("plain-principal", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> authService.me()
        );

        assertEquals("Unauthenticated", exception.getMessage());
    }
}