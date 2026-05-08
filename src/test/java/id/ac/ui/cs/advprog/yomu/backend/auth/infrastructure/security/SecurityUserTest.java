package id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security;

// Import static dari Factory kita
import static id.ac.ui.cs.advprog.yomu.backend.auth.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import org.junit.jupiter.api.Test;

class SecurityUserTest {

  @Test
  void shouldReturnWrappedUser() {
    User user = createDummyUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertEquals(user, securityUser.getUser());
  }

  @Test
  void shouldReturnCorrectAuthorities() {
    User user = createDummyUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertEquals(1, securityUser.getAuthorities().size());
    assertEquals("ROLE_USER", securityUser.getAuthorities().get(0).getAuthority());
  }

  @Test
  void shouldReturnPasswordFromUser() {
    User user = createDummyUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertEquals("hashed-password", securityUser.getPassword());
  }

  @Test
  void shouldReturnUsernameFromUser() {
    User user = createDummyUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertEquals(DEFAULT_USERNAME, securityUser.getUsername());
  }

  @Test
  void shouldAlwaysBeNonExpired() {
    User user = createDummyUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertTrue(securityUser.isAccountNonExpired());
  }

  @Test
  void shouldAlwaysBeNonLocked() {
    User user = createDummyUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertTrue(securityUser.isAccountNonLocked());
  }

  @Test
  void shouldAlwaysHaveNonExpiredCredentials() {
    User user = createDummyUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertTrue(securityUser.isCredentialsNonExpired());
  }

  @Test
  void shouldAlwaysBeEnabled() {
    User user = createDummyUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertTrue(securityUser.isEnabled());
  }
}
