package id.ac.ui.cs.advprog.yomu.backend.social.api;

import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.ClanNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "id.ac.ui.cs.advprog.yomu.backend.social.api")
public class SocialExceptionHandler {

  @ExceptionHandler(ClanNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleNotFound(ClanNotFoundException ex) {
    return build(HttpStatus.NOT_FOUND, ex);
  }

  private ResponseEntity<ProblemDetail> build(HttpStatus status, RuntimeException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
    return ResponseEntity.status(status).body(problem);
  }
}
