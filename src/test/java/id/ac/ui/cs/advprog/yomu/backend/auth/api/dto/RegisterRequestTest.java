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
    assertEquals(null, request.getDisplayName());
    assertEquals(null, request.getEmail());
    assertEquals(null, request.getPhoneNumber());
    assertEquals(null, request.getPassword());
  }

  @Test
  void shouldCreateRegisterRequestWithAllArgsConstructor() {
    String username = "rifqi";
    String displayName = "Rifqi Ahmad";
    String email = "rifqi@mail.com";
    String phoneNumber = "08123";
    String password = "secret123";

    // Panggil dengan 5 argumen
    RegisterRequest request =
        new RegisterRequest(username, displayName, email, phoneNumber, password);

    assertEquals(username, request.getUsername());
    assertEquals(displayName, request.getDisplayName());
    assertEquals(email, request.getEmail());
    assertEquals(phoneNumber, request.getPhoneNumber());
    assertEquals(password, request.getPassword());
  }

  @Test
  void shouldPassValidationWhenFieldsAreValid() {
    RegisterRequest request =
        new RegisterRequest("rifqi", "Rifqi Ahmad", "rifqi@mail.com", "08123", "secret123");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldFailValidationWhenUsernameIsBlank() {
    RegisterRequest request =
        new RegisterRequest("", "Rifqi Ahmad", "rifqi@mail.com", "08123", "secret123");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
  }

  @Test
  void shouldFailValidationWhenUsernameTooShort() {
    RegisterRequest request =
        new RegisterRequest("ab", "Rifqi Ahmad", "rifqi@mail.com", "08123", "secret123");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
  }

  @Test
  void shouldFailValidationWhenUsernameTooLong() {
    RegisterRequest request =
        new RegisterRequest("a".repeat(41), "Rifqi Ahmad", "rifqi@mail.com", "08123", "secret123");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
  }

  @Test
  void shouldFailValidationWhenEmailIsInvalid() {
    RegisterRequest request =
        new RegisterRequest("rifqi", "Rifqi Ahmad", "not-an-email", "08123", "secret123");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
  }

  @Test
  void shouldFailValidationWhenPasswordIsBlank() {
    RegisterRequest request =
        new RegisterRequest("rifqi", "Rifqi Ahmad", "rifqi@mail.com", "08123", "");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
  }

  @Test
  void shouldFailValidationWhenPasswordTooShort() {
    RegisterRequest request =
        new RegisterRequest("rifqi", "Rifqi Ahmad", "rifqi@mail.com", "08123", "12345");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
  }

  @Test
  void shouldFailValidationWhenPasswordTooLong() {
    RegisterRequest request =
        new RegisterRequest("rifqi", "Rifqi Ahmad", "rifqi@mail.com", "08123", "a".repeat(101));

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
  }
}
