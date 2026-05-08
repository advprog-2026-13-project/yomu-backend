package id.ac.ui.cs.advprog.yomu.backend.forum.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReactRequest {
  @NotBlank private String type;
}
