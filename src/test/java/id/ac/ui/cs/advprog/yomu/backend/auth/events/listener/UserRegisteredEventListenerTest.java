package id.ac.ui.cs.advprog.yomu.backend.auth.events.listener;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.auth.events.UserRegisteredEvent;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserRegisteredEventListenerTest {

  private final UserRegisteredEventListener listener = new UserRegisteredEventListener();

  @Test
  void shouldHandleEventWithoutException() {
    var event = new UserRegisteredEvent(UUID.randomUUID(), "bob", "Bob User");

    assertDoesNotThrow(() -> listener.handleUserRegistered(event));
  }
}
