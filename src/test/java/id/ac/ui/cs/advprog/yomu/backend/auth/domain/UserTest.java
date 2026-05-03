package id.ac.ui.cs.advprog.yomu.backend.auth.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserTest {
  @Test
  void testUserEntityMethods() {
    User user = new User("rifqi", "Rifqi Ilham", "rifqi@mail.com", "08123", "hashed", Role.USER);
    UUID id = UUID.randomUUID();
    user.setId(id);
    user.setGoogleSub("sub123");

    assertEquals(id, user.getId());
    assertEquals("rifqi", user.getUsername());
    assertEquals("Rifqi Ilham", user.getDisplayName());
    assertEquals("rifqi@mail.com", user.getEmail());
    assertEquals("08123", user.getPhoneNumber());
    assertEquals("hashed", user.getPasswordHash());
    assertEquals(Role.USER, user.getRole());
    assertEquals("sub123", user.getGoogleSub());

    user.setDisplayName("New Name");
    assertEquals("New Name", user.getDisplayName());

    assertNotNull(user.toString());
    assertTrue(user.hashCode() != 0);
    assertSame(user, user);
    assertNotNull(user);
    assertNotEquals(user, new Object());
  }
}
