package id.ac.ui.cs.advprog.yomu.backend.social.application.exception;

public class JoinRequestAlreadyResolvedException extends RuntimeException {
  public JoinRequestAlreadyResolvedException(String message) {
    super(message);
  }
}
