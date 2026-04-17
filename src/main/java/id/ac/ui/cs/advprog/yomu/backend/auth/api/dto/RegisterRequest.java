package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import jakarta.validation.constraints.*;

public class RegisterRequest {
  @NotBlank
  @Size(min = 3, max = 40)
  private String username;

  @NotBlank @Email private String email;

  @NotBlank
  @Size(min = 6, max = 100)
  private String password;

  public RegisterRequest() {}

  public RegisterRequest(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }
}
