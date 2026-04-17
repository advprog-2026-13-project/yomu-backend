package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
  @NotBlank private String identifier;

  @NotBlank private String password;
}
