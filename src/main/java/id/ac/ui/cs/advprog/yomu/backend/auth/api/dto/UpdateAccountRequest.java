package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAccountRequest {
  @Size(min = 3, max = 40)
  private String username;

  @Size(max = 100)
  private String displayName;

  @Email private String email;

  private String phoneNumber;

  @Size(min = 6, max = 100)
  private String password;
}
