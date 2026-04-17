package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
  private String accessToken;
}
