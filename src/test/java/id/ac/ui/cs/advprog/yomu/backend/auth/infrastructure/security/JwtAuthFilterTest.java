package id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import jakarta.servlet.FilterChain;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthFilterTest {

  private JwtService jwtService;
  private UserRepository userRepository;
  private JwtAuthFilter jwtAuthFilter;
  private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    jwtService = mock(JwtService.class);
    userRepository = mock(UserRepository.class);
    filterChain = mock(FilterChain.class);
    jwtAuthFilter = new JwtAuthFilter(jwtService, userRepository);
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(jwtService);
    verifyNoInteractions(userRepository);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void shouldContinueFilterChainWhenAuthorizationHeaderIsNotBearer() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Basic abc123");
    MockHttpServletResponse response = new MockHttpServletResponse();

    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(jwtService);
    verifyNoInteractions(userRepository);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void shouldSetAuthenticationWhenTokenIsValidAndUserExists() throws Exception {
    UUID userId = UUID.randomUUID();
    User user =
        new User(
            "rifqi", "Rifqi Ahmad", "rifqi@mail.com", "0812345678", "hashed-password", Role.USER);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer valid-token");
    MockHttpServletResponse response = new MockHttpServletResponse();

    when(jwtService.parse("valid-token"))
        .thenReturn(new JwtService.Payload(userId.toString(), "rifqi", "USER"));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    verify(jwtService).parse("valid-token");
    verify(userRepository).findById(userId);
    verify(filterChain).doFilter(request, response);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    assertInstanceOf(
        SecurityUser.class, SecurityContextHolder.getContext().getAuthentication().getPrincipal());

    SecurityUser principal =
        (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    assertEquals("rifqi", principal.getUsername());
    assertTrue(
        principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
  }

  @Test
  void shouldNotSetAuthenticationWhenTokenIsValidButUserDoesNotExist() throws Exception {
    UUID userId = UUID.randomUUID();

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer valid-token");
    MockHttpServletResponse response = new MockHttpServletResponse();

    when(jwtService.parse("valid-token"))
        .thenReturn(new JwtService.Payload(userId.toString(), "rifqi", "USER"));
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    verify(jwtService).parse("valid-token");
    verify(userRepository).findById(userId);
    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void shouldClearContextWhenTokenParsingFails() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer invalid-token");
    MockHttpServletResponse response = new MockHttpServletResponse();

    SecurityContextHolder.getContext()
        .setAuthentication(mock(org.springframework.security.core.Authentication.class));

    when(jwtService.parse("invalid-token")).thenThrow(new RuntimeException("Invalid token"));

    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    verify(jwtService).parse("invalid-token");
    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
