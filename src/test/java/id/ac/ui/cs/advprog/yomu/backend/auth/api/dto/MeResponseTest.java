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
    String displayName = "Rifqi Ilham";
    String email = "rifqi@mail.com";
    String phoneNumber = "08123456789";
    Role role = Role.USER;
    ;

    MeResponse response = new MeResponse(id, username, displayName, email, phoneNumber, role);

    assertEquals(id, response.getId());
    assertEquals(username, response.getUsername());
    assertEquals(displayName, response.getDisplayName());
    assertEquals(email, response.getEmail());
    assertEquals(phoneNumber, response.getPhoneNumber());
    assertEquals(role, response.getRole());
  }
}
