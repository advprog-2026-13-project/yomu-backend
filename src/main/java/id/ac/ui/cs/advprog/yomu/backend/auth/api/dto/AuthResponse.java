package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

public class AuthResponse {
  private final String accessToken;

  public AuthResponse(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getAccessToken() {
    return accessToken;
  }
}
