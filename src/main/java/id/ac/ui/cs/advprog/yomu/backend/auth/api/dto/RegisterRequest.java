package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
  @NotBlank
  @Size(min = 3, max = 40)
  private String username;

  @NotBlank @Email private String email;

  @NotBlank
  @Size(min = 6, max = 100)
  private String password;
}
