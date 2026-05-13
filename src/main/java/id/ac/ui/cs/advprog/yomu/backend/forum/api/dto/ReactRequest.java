package id.ac.ui.cs.advprog.yomu.backend.forum.api.dto;

import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReactRequest {
  @NotNull private ReactionType type;
}
