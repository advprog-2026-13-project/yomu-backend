package id.ac.ui.cs.advprog.yomu.backend.auth.api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;

class AuthExceptionHandlerTest {

  private final AuthExceptionHandler handler = new AuthExceptionHandler();

  @Test
  void shouldReturnBadRequestForIllegalArgumentException() {
    var response = handler.handleIllegalArgument(new IllegalArgumentException("Invalid input"));

    assertEquals(400, response.getStatusCode().value());
    assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("Bad Request", body.get("title"));
    assertEquals(400, body.get("status"));
    assertEquals("Invalid input", body.get("detail"));
  }

  @Test
  void shouldReturnUnauthorizedForIllegalStateException() {
    var response = handler.handleIllegalState(new IllegalStateException("Unauthenticated"));

    assertEquals(401, response.getStatusCode().value());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("Unauthorized", body.get("title"));
    assertEquals(401, body.get("status"));
  }

  @Test
  void shouldReturnUnauthorizedForBadCredentialsException() {
    var response = handler.handleBadCredentials(new BadCredentialsException("Invalid credentials"));

    assertEquals(401, response.getStatusCode().value());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("Invalid Credentials", body.get("title"));
  }

  @Test
  void shouldIncludeTimestampInResponse() {
    var response = handler.handleIllegalArgument(new IllegalArgumentException("test"));

    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertTrue(body.containsKey("timestamp"));
    assertNotNull(body.get("type"));
  }
}
