package id.ac.ui.cs.advprog.yomu.backend.forum.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import id.ac.ui.cs.advprog.yomu.backend.forum.application.exception.ForumBadRequestException;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.exception.ForumForbiddenException;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.exception.ForumNotFoundException;

@RestControllerAdvice(basePackages = "id.ac.ui.cs.advprog.yomu.backend.forum.api")
public class ForumExceptionHandler {

  @ExceptionHandler(ForumNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleNotFound(ForumNotFoundException ex) {
    return build(HttpStatus.NOT_FOUND, ex);
  }

  @ExceptionHandler(ForumForbiddenException.class)
  public ResponseEntity<ProblemDetail> handleForbidden(ForumForbiddenException ex) {
    return build(HttpStatus.FORBIDDEN, ex);
  }

  @ExceptionHandler(ForumBadRequestException.class)
  public ResponseEntity<ProblemDetail> handleBadRequest(ForumBadRequestException ex) {
    return build(HttpStatus.BAD_REQUEST, ex);
  }

  private ResponseEntity<ProblemDetail> build(HttpStatus status, RuntimeException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
    return ResponseEntity.status(status).body(problem);
  }
}
