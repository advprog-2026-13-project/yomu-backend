package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import static id.ac.ui.cs.advprog.yomu.backend.auth.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import org.junit.jupiter.api.Test;

class MeResponseTest {

  @Test
  void shouldCreateMeResponseCorrectly() {
    User user = createDummyUser();

    MeResponse response = createMeResponse(user);

    assertEquals(user.getId(), response.getId());
    assertEquals(DEFAULT_USERNAME, response.getUsername());
    assertEquals(DEFAULT_DISPLAY_NAME, response.getDisplayName());
    assertEquals(DEFAULT_EMAIL, response.getEmail());
    assertEquals(DEFAULT_PHONE, response.getPhoneNumber());
    assertEquals(user.getRole(), response.getRole());
  }
}
