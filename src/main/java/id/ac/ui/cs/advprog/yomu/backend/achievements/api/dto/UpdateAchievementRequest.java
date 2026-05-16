package id.ac.ui.cs.advprog.yomu.backend.achievements.api.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAchievementRequest {
  private String name;

  private String description;

  @Min(1)
  private Integer milestone;
}
