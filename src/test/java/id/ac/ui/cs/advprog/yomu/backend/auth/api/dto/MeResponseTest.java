package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MeResponseTest {

  @Test
  void shouldCreateMeResponseCorrectly() {
    UUID id = UUID.randomUUID();
    String username = "rifqi";
    String email = "rifqi@mail.com";
    Role role = Role.USER;

    MeResponse response = new MeResponse(id, username, email, role);

    assertEquals(id, response.getId());
    assertEquals(username, response.getUsername());
    assertEquals(email, response.getEmail());
    assertEquals(role, response.getRole());
  }
}
