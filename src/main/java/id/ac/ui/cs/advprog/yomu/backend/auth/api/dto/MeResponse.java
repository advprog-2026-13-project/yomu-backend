package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeResponse {
  private UUID id;
  private String username;
  private String email;
  private Role role;
}
