package id.ac.ui.cs.advprog.yomu.backend.forum.application.exception;

public class ForumNotFoundException extends RuntimeException {
  public ForumNotFoundException(String message) {
    super(message);
  }
}
