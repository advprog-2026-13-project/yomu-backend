package id.ac.ui.cs.advprog.yomu.backend.forum.application.port.in;

import id.ac.ui.cs.advprog.yomu.backend.forum.application.dto.CommentView;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;
import java.util.List;
import java.util.UUID;

public interface ForumUseCase {
  List<CommentView> getComments(UUID readingId);

  CommentView postComment(UUID readingId, UUID userId, String content);

  CommentView replyToComment(UUID parentCommentId, UUID userId, String content);

  void editComment(UUID commentId, UUID requesterId, String newContent);

  void deleteComment(UUID commentId, UUID requesterId, boolean isAdmin);

  void toggleReaction(UUID commentId, UUID userId, ReactionType type);
}
