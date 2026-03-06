package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AuthResponseTest {

  @Test
  void shouldCreateAuthResponseCorrectly() {
    String accessToken = "dummy-jwt-token";

    AuthResponse response = new AuthResponse(accessToken);

    assertEquals(accessToken, response.getAccessToken());
  }
}
