package id.ac.ui.cs.advprog.yomu.backend.auth.events;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserRegisteredEventTest {

  @Test
  void shouldStoreAllFields() {
    UUID userId = UUID.randomUUID();
    var event = new UserRegisteredEvent(userId, "bob", "Bob User");

    assertEquals(userId, event.getUserId());
    assertEquals("bob", event.getUsername());
    assertEquals("Bob User", event.getDisplayName());
  }
}
