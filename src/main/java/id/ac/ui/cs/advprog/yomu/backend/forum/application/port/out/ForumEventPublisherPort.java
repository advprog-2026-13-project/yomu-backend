package id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out;

import java.util.UUID;

public interface ForumEventPublisherPort {
  void publishCommentCreated(UUID commentId, UUID authorId, UUID readingId);

  void publishCommentDeleted(UUID commentId, UUID requesterId, boolean isAdmin);
}
