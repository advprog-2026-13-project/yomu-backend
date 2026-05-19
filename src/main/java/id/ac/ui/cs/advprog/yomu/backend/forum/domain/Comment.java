package id.ac.ui.cs.advprog.yomu.backend.forum.domain;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

  private UUID id;

  private UUID readingId;

  private UUID authorId;

  private String authorName;

  private UUID parentId;

  private String content;

  private boolean deleted = false;

  private Instant createdAt;

  private Instant editedAt;
}
