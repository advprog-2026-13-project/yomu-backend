package id.ac.ui.cs.advprog.yomu.backend.forum.events;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDeletedEvent {
  private UUID commentId;
  private UUID requesterId;
  private boolean isAdmin;
}
