package id.ac.ui.cs.advprog.yomu.backend.social.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateClanRequest {

  @NotBlank
  @Size(min = 3, max = 50)
  private String name;
}