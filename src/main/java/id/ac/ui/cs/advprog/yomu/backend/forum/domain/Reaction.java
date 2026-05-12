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
public class Reaction {

  private UUID id;

  private UUID commentId;

  private UUID userId;

  private ReactionType type;

  private Instant createdAt;
}
