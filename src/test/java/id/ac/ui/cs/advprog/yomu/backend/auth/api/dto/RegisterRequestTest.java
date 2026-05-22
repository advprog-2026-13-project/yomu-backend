package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import static id.ac.ui.cs.advprog.yomu.backend.auth.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    assertNull(request.getUsername());
    assertNull(request.getDisplayName());
    assertNull(request.getEmail());
    assertNull(request.getPhoneNumber());
    assertNull(request.getPassword());
  }

  @Test
  void shouldCreateRegisterRequestWithAllArgsConstructor() {
    RegisterRequest request =
        new RegisterRequest(
            DEFAULT_USERNAME, DEFAULT_DISPLAY_NAME, DEFAULT_EMAIL, DEFAULT_PHONE, DEFAULT_PASSWORD);

    assertEquals(DEFAULT_USERNAME, request.getUsername());
    assertEquals(DEFAULT_DISPLAY_NAME, request.getDisplayName());
    assertEquals(DEFAULT_EMAIL, request.getEmail());
    assertEquals(DEFAULT_PHONE, request.getPhoneNumber());
    assertEquals(DEFAULT_PASSWORD, request.getPassword());
  }

  @Test
  void shouldPassValidationWhenFieldsAreValid() {
    RegisterRequest request = createRegisterRequest();

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldFailValidationWhenUsernameIsBlank() {
    RegisterRequest request =
        new RegisterRequest(
            "", DEFAULT_DISPLAY_NAME, DEFAULT_EMAIL, DEFAULT_PHONE, DEFAULT_PASSWORD);

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
  }

  @Test
  void shouldFailValidationWhenUsernameTooShort() {
    RegisterRequest request =
        new RegisterRequest(
            "ab", DEFAULT_DISPLAY_NAME, DEFAULT_EMAIL, DEFAULT_PHONE, DEFAULT_PASSWORD);

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
  }

  @Test
  void shouldFailValidationWhenUsernameTooLong() {
    RegisterRequest request =
        new RegisterRequest(
            "a".repeat(41), DEFAULT_DISPLAY_NAME, DEFAULT_EMAIL, DEFAULT_PHONE, DEFAULT_PASSWORD);

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
  }

  @Test
  void shouldFailValidationWhenEmailIsInvalid() {
    RegisterRequest request =
        new RegisterRequest(
            DEFAULT_USERNAME,
            DEFAULT_DISPLAY_NAME,
            "not-an-email",
            DEFAULT_PHONE,
            DEFAULT_PASSWORD);

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
  }

  @Test
  void shouldFailValidationWhenPasswordIsBlank() {
    RegisterRequest request =
        new RegisterRequest(
            DEFAULT_USERNAME, DEFAULT_DISPLAY_NAME, DEFAULT_EMAIL, DEFAULT_PHONE, "");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
  }

  @Test
  void shouldFailValidationWhenPasswordTooShort() {
    RegisterRequest request =
        new RegisterRequest(
            DEFAULT_USERNAME, DEFAULT_DISPLAY_NAME, DEFAULT_EMAIL, DEFAULT_PHONE, "12345");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
  }

  @Test
  void shouldFailValidationWhenPasswordTooLong() {
    RegisterRequest request =
        new RegisterRequest(
            DEFAULT_USERNAME, DEFAULT_DISPLAY_NAME, DEFAULT_EMAIL, DEFAULT_PHONE, "a".repeat(101));

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
  }
}
