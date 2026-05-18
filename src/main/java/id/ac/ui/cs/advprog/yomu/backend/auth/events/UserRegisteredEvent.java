package id.ac.ui.cs.advprog.yomu.backend.auth.events;

import java.util.UUID;

public class UserRegisteredEvent {

  private final UUID userId;
  private final String username;
  private final String displayName;

  public UserRegisteredEvent(UUID userId, String username, String displayName) {
    this.userId = userId;
    this.username = username;
    this.displayName = displayName;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getUsername() {
    return username;
  }

  public String getDisplayName() {
    return displayName;
  }
}
