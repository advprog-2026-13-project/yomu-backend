package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
  @NotBlank
  @Size(min = 3, max = 40)
  private String username;

  @NotBlank
  @Size(max = 100)
  private String displayName;

  @Email private String email;
  private String phoneNumber;

  @NotBlank
  @Size(min = 6, max = 100)
  private String password;
}
