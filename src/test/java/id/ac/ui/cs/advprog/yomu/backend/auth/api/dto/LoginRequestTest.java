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

    assertEquals(null, request.getIdentifier());
    assertEquals(null, request.getPassword());
  }

  @Test
  void shouldCreateLoginRequestWithAllArgsConstructor() {
    String identifier = "rifqi";
    String password = "secret123";

    LoginRequest request = new LoginRequest(identifier, password);

    assertEquals(identifier, request.getIdentifier());
    assertEquals(password, request.getPassword());
  }

  @Test
  void shouldPassValidationWhenFieldsAreValid() {
    LoginRequest request = new LoginRequest("rifqi", "secret123");

    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldFailValidationWhenIdentifierIsBlank() {
    LoginRequest request = new LoginRequest("", "secret123");

    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

    assertEquals(1, violations.size());
    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("identifier")));
  }

  @Test
  void shouldFailValidationWhenPasswordIsBlank() {
    LoginRequest request = new LoginRequest("rifqi", "");

    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

    assertEquals(1, violations.size());
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
