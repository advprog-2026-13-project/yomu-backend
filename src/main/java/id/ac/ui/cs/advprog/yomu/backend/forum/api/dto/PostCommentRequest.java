package id.ac.ui.cs.advprog.yomu.backend.forum.api.dto;

import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ForumConstraints;
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
public class PostCommentRequest {
  @NotBlank
  @Size(max = ForumConstraints.MAX_CONTENT_LENGTH)
  private String content;
}
