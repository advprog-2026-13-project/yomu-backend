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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

  // --- INI TRIKNYA ---
  @ParameterizedTest
  @CsvSource({"'', 'password123', identifier", "'user123', '', password", "'', '', identifier"})
  void shouldFailValidationForInvalidInputs(
      String identifier, String password, String expectedField) {
    LoginRequest request = new LoginRequest(identifier, password);
    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty(), "Violations should not be empty for invalid inputs");
    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(expectedField)),
        "Should have violation for field: " + expectedField);
  }
}
