package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginRateLimiterTest {

  private LoginRateLimiter rateLimiter;

  @BeforeEach
  void setUp() {
    rateLimiter = new LoginRateLimiter();
  }

  @Test
  void shouldNotBlockInitially() {
    assertFalse(rateLimiter.isBlocked("user1"));
  }

  @Test
  void shouldBlockAfterMaxAttempts() {
    for (int i = 0; i < 5; i++) {
      rateLimiter.recordFailure("user1");
    }

    assertTrue(rateLimiter.isBlocked("user1"));
  }

  @Test
  void shouldNotBlockBeforeMaxAttempts() {
    for (int i = 0; i < 4; i++) {
      rateLimiter.recordFailure("user1");
    }

    assertFalse(rateLimiter.isBlocked("user1"));
  }

  @Test
  void shouldResetBlockedUser() {
    for (int i = 0; i < 5; i++) {
      rateLimiter.recordFailure("user1");
    }
    assertTrue(rateLimiter.isBlocked("user1"));

    rateLimiter.reset("user1");

    assertFalse(rateLimiter.isBlocked("user1"));
  }

  @Test
  void shouldTrackDifferentUsersSeparately() {
    rateLimiter.recordFailure("user1");
    rateLimiter.recordFailure("user1");
    rateLimiter.recordFailure("user2");

    assertFalse(rateLimiter.isBlocked("user1"));
    assertFalse(rateLimiter.isBlocked("user2"));
  }

  @Test
  void resetOnNonexistentUserDoesNotThrow() {
    assertDoesNotThrow(() -> rateLimiter.reset("nonexistent"));
  }
}
