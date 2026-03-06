package id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class JwtServiceTest {

  private static final String SECRET = "12345678901234567890123456789012";

  @Test
  void shouldGenerateAndParseTokenCorrectly() {
    JwtService jwtService = new JwtService(SECRET, 120);

    UUID userId = UUID.randomUUID();
    User user = Mockito.mock(User.class);

    when(user.getId()).thenReturn(userId);
    when(user.getUsername()).thenReturn("rifqi");
    when(user.getRole()).thenReturn(Role.USER);

    String token = jwtService.generateToken(user);
    JwtService.Payload payload = jwtService.parse(token);

    assertEquals(userId.toString(), payload.userId());
    assertEquals("rifqi", payload.username());
    assertEquals("USER", payload.role());
  }

  @Test
  void shouldThrowExceptionWhenParsingInvalidToken() {
    JwtService jwtService = new JwtService(SECRET, 120);

    assertThrows(Exception.class, () -> jwtService.parse("invalid-token"));
  }
}
