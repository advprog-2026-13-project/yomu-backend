package id.ac.ui.cs.advprog.yomu.backend.achievements.api.dto;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAchievementRequest {
    @NotBlank
    private String name;
    
    private String description;
    
    @NotNull
    private AchievementType type;
    
    @Min(1)
    private int milestone;
}
