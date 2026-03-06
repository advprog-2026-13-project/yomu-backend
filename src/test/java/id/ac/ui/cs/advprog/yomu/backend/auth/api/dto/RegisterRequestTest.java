package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegisterRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldCreateRegisterRequestWithNoArgsConstructor() {
    RegisterRequest request = new RegisterRequest();

    assertEquals(null, request.getUsername());
    assertEquals(null, request.getEmail());
    assertEquals(null, request.getPassword());
  }

  @Test
  void shouldCreateRegisterRequestWithAllArgsConstructor() {
    String username = "rifqi";
    String email = "rifqi@mail.com";
    String password = "secret123";

    RegisterRequest request = new RegisterRequest(username, email, password);

    assertEquals(username, request.getUsername());
    assertEquals(email, request.getEmail());
    assertEquals(password, request.getPassword());
  }

  @Test
  void shouldPassValidationWhenFieldsAreValid() {
    RegisterRequest request = new RegisterRequest("rifqi", "rifqi@mail.com", "secret123");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldFailValidationWhenUsernameIsBlank() {
    RegisterRequest request = new RegisterRequest("", "rifqi@mail.com", "secret123");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
  }

  @Test
  void shouldFailValidationWhenUsernameTooShort() {
    RegisterRequest request = new RegisterRequest("ab", "rifqi@mail.com", "secret123");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
  }

  @Test
  void shouldFailValidationWhenUsernameTooLong() {
    RegisterRequest request = new RegisterRequest("a".repeat(41), "rifqi@mail.com", "secret123");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
  }

  @Test
  void shouldFailValidationWhenEmailIsBlank() {
    RegisterRequest request = new RegisterRequest("rifqi", "", "secret123");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
  }

  @Test
  void shouldFailValidationWhenEmailIsInvalid() {
    RegisterRequest request = new RegisterRequest("rifqi", "not-an-email", "secret123");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
  }

  @Test
  void shouldFailValidationWhenPasswordIsBlank() {
    RegisterRequest request = new RegisterRequest("rifqi", "rifqi@mail.com", "");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
  }

  @Test
  void shouldFailValidationWhenPasswordTooShort() {
    RegisterRequest request = new RegisterRequest("rifqi", "rifqi@mail.com", "12345");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
  }

  @Test
  void shouldFailValidationWhenPasswordTooLong() {
    RegisterRequest request = new RegisterRequest("rifqi", "rifqi@mail.com", "a".repeat(101));

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
  }
}
