package id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import org.junit.jupiter.api.Test;

class SecurityUserTest {

  @Test
  void shouldReturnWrappedUser() {
    User user = new User("rifqi", "rifqi@mail.com", "hashed-password", Role.USER);

    SecurityUser securityUser = new SecurityUser(user);

    assertEquals(user, securityUser.getUser());
  }

  @Test
  void shouldReturnCorrectAuthorities() {
    User user = new User("rifqi", "rifqi@mail.com", "hashed-password", Role.USER);

    SecurityUser securityUser = new SecurityUser(user);

    assertEquals(1, securityUser.getAuthorities().size());
    assertEquals("ROLE_USER", securityUser.getAuthorities().get(0).getAuthority());
  }

  @Test
  void shouldReturnPasswordFromUser() {
    User user = new User("rifqi", "rifqi@mail.com", "hashed-password", Role.USER);

    SecurityUser securityUser = new SecurityUser(user);

    assertEquals("hashed-password", securityUser.getPassword());
  }

  @Test
  void shouldReturnUsernameFromUser() {
    User user = new User("rifqi", "rifqi@mail.com", "hashed-password", Role.USER);

    SecurityUser securityUser = new SecurityUser(user);

    assertEquals("rifqi", securityUser.getUsername());
  }

  @Test
  void shouldAlwaysBeNonExpired() {
    User user = new User("rifqi", "rifqi@mail.com", "hashed-password", Role.USER);

    SecurityUser securityUser = new SecurityUser(user);

    assertTrue(securityUser.isAccountNonExpired());
  }

  @Test
  void shouldAlwaysBeNonLocked() {
    User user = new User("rifqi", "rifqi@mail.com", "hashed-password", Role.USER);

    SecurityUser securityUser = new SecurityUser(user);

    assertTrue(securityUser.isAccountNonLocked());
  }

  @Test
  void shouldAlwaysHaveNonExpiredCredentials() {
    User user = new User("rifqi", "rifqi@mail.com", "hashed-password", Role.USER);

    SecurityUser securityUser = new SecurityUser(user);

    assertTrue(securityUser.isCredentialsNonExpired());
  }

  @Test
  void shouldAlwaysBeEnabled() {
    User user = new User("rifqi", "rifqi@mail.com", "hashed-password", Role.USER);

    SecurityUser securityUser = new SecurityUser(user);

    assertTrue(securityUser.isEnabled());
  }
}
