package id.ac.ui.cs.advprog.yomu.backend.auth.api;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "id.ac.ui.cs.advprog.yomu.backend.auth")
public class AuthExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(AuthExceptionHandler.class);

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    log.warn("Bad request in auth module: {}", ex.getMessage());
    return problem(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
    log.warn("Unauthorized access: {}", ex.getMessage());
    return problem(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage());
  }

  @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
  public ResponseEntity<Map<String, Object>> handleBadCredentials(
      org.springframework.security.authentication.BadCredentialsException ex) {
    log.warn("Bad credentials: {}", ex.getMessage());
    return problem(HttpStatus.UNAUTHORIZED, "Invalid Credentials", ex.getMessage());
  }

  private ResponseEntity<Map<String, Object>> problem(
      HttpStatus status, String title, String detail) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put(
        "type", URI.create("https://yomu.dev/problems/" + title.toLowerCase().replace(" ", "-")));
    body.put("title", title);
    body.put("status", status.value());
    body.put("detail", detail);
    body.put("timestamp", Instant.now().toString());

    return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(body);
  }
}
