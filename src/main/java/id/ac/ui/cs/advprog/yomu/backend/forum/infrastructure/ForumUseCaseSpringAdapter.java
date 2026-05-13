package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.ac.ui.cs.advprog.yomu.backend.forum.application.ForumService;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.dto.CommentView;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.in.ForumUseCase;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.CommentRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.ForumEventPublisherPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.ReactionRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.UserPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;

@Service
public class ForumUseCaseSpringAdapter implements ForumUseCase {

  private final ForumService forumService;

  public ForumUseCaseSpringAdapter(
      CommentRepositoryPort commentRepository,
      ReactionRepositoryPort reactionRepository,
      UserPort userRepository,
      ForumEventPublisherPort eventPublisher) {
    this.forumService =
        new ForumService(commentRepository, reactionRepository, userRepository, eventPublisher);
  }

  @Transactional(readOnly = true)
  @Override
  public List<CommentView> getComments(UUID readingId) {
    return forumService.getComments(readingId);
  }

  @Transactional
  @Override
  public CommentView postComment(UUID readingId, UUID userId, String content) {
    return forumService.postComment(readingId, userId, content);
  }

  @Transactional
  @Override
  public CommentView replyToComment(UUID parentCommentId, UUID userId, String content) {
    return forumService.replyToComment(parentCommentId, userId, content);
  }

  @Transactional
  @Override
  public void editComment(UUID commentId, UUID requesterId, String newContent) {
    forumService.editComment(commentId, requesterId, newContent);
  }

  @Transactional
  @Override
  public void deleteComment(UUID commentId, UUID requesterId, boolean isAdmin) {
    forumService.deleteComment(commentId, requesterId, isAdmin);
  }

  @Transactional
  @Override
  public void toggleReaction(UUID commentId, UUID userId, ReactionType type) {
    forumService.toggleReaction(commentId, userId, type);
  }
}
