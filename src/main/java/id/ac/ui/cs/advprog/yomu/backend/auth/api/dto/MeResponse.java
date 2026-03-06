package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import java.util.UUID;

public class MeResponse {
  private final UUID id;
  private final String username;
  private final String email;
  private final Role role;

  public MeResponse(UUID id, String username, String email, Role role) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.role = role;
  }

  public UUID getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public Role getRole() {
    return role;
  }
}
