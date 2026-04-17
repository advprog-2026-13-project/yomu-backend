package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import static id.ac.ui.cs.advprog.yomu.backend.auth.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldCreateLoginRequestWithNoArgsConstructor() {
    LoginRequest request = new LoginRequest();
    assertNull(request.getIdentifier());
    assertNull(request.getPassword());
  }

  @Test
  void shouldCreateLoginRequestWithAllArgsConstructor() {
    LoginRequest request = new LoginRequest(DEFAULT_USERNAME, DEFAULT_PASSWORD);

    assertEquals(DEFAULT_USERNAME, request.getIdentifier());
    assertEquals(DEFAULT_PASSWORD, request.getPassword());
  }

  @Test
  void shouldPassValidationWhenFieldsAreValid() {
    LoginRequest request = createLoginRequest();

    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldFailValidationWhenIdentifierIsBlank() {
    LoginRequest request = new LoginRequest("", DEFAULT_PASSWORD);

    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("identifier")));
  }

  @Test
  void shouldFailValidationWhenPasswordIsBlank() {
    LoginRequest request = new LoginRequest(DEFAULT_USERNAME, "");

    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
  }

  @Test
  void shouldFailValidationWhenAllFieldsAreBlank() {
    LoginRequest request = new LoginRequest("", "");
    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
    assertEquals(2, violations.size());
  }
}
