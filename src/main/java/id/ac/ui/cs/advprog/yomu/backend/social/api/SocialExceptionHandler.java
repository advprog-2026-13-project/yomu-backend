package id.ac.ui.cs.advprog.yomu.backend.social.api;

import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.AlreadyMemberException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.ClanNotFoundException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.DuplicateJoinRequestException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.JoinRequestAlreadyResolvedException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.NotClanLeaderException;
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

  @ExceptionHandler(NotClanLeaderException.class)
  public ResponseEntity<ProblemDetail> handleForbidden(NotClanLeaderException ex) {
    return build(HttpStatus.FORBIDDEN, ex);
  }

  @ExceptionHandler(AlreadyMemberException.class)
  public ResponseEntity<ProblemDetail> handleAlreadyMember(AlreadyMemberException ex) {
    return build(HttpStatus.CONFLICT, ex);
  }

  @ExceptionHandler(DuplicateJoinRequestException.class)
  public ResponseEntity<ProblemDetail> handleDuplicateRequest(DuplicateJoinRequestException ex) {
    return build(HttpStatus.CONFLICT, ex);
  }

  @ExceptionHandler(JoinRequestAlreadyResolvedException.class)
  public ResponseEntity<ProblemDetail> handleAlreadyResolved(
      JoinRequestAlreadyResolvedException ex) {
    return build(HttpStatus.CONFLICT, ex);
  }

  private ResponseEntity<ProblemDetail> build(HttpStatus status, RuntimeException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
    return ResponseEntity.status(status).body(problem);
  }
}
