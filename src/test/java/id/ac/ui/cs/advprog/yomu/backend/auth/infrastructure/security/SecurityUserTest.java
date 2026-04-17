package id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import org.junit.jupiter.api.Test;

class SecurityUserTest {

  private User createTestUser() {
    return new User(
        "rifqi", "Rifqi Ilham", "rifqi@mail.com", "0812345678", "hashed-password", Role.USER);
  }

  @Test
  void shouldReturnWrappedUser() {
    User user = createTestUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertEquals(user, securityUser.getUser());
  }

  @Test
  void shouldReturnCorrectAuthorities() {
    User user = createTestUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertEquals(1, securityUser.getAuthorities().size());
    assertEquals("ROLE_USER", securityUser.getAuthorities().get(0).getAuthority());
  }

  @Test
  void shouldReturnPasswordFromUser() {
    User user = createTestUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertEquals("hashed-password", securityUser.getPassword());
  }

  @Test
  void shouldReturnUsernameFromUser() {
    User user = createTestUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertEquals("rifqi", securityUser.getUsername());
  }

  @Test
  void shouldAlwaysBeNonExpired() {
    User user = createTestUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertTrue(securityUser.isAccountNonExpired());
  }

  @Test
  void shouldAlwaysBeNonLocked() {
    User user = createTestUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertTrue(securityUser.isAccountNonLocked());
  }

  @Test
  void shouldAlwaysHaveNonExpiredCredentials() {
    User user = createTestUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertTrue(securityUser.isCredentialsNonExpired());
  }

  @Test
  void shouldAlwaysBeEnabled() {
    User user = createTestUser();
    SecurityUser securityUser = new SecurityUser(user);

    assertTrue(securityUser.isEnabled());
  }
}
