package id.ac.ui.cs.advprog.yomu.backend.forum.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.yomu.backend.forum.application.dto.CommentView;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.exception.ForumBadRequestException;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.exception.ForumForbiddenException;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.exception.ForumNotFoundException;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.CommentRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.ForumEventPublisherPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.ReactionRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Comment;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Reaction;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ForumServiceTest {

  @Mock private CommentRepositoryPort commentRepository;
  @Mock private ReactionRepositoryPort reactionRepository;
  @Mock private ForumEventPublisherPort eventPublisher;

  @InjectMocks private ForumService forumService;

  private UUID readingId;
  private UUID userId;
  private UUID commentId;

  @BeforeEach
  @SuppressWarnings("unused")
  void setUp() {
    readingId = UUID.randomUUID();
    userId = UUID.randomUUID();
    commentId = UUID.randomUUID();
  }

  // ─────────────────────────── helpers ────────────────────────────

  private Comment buildComment(
      UUID id, UUID reading, UUID author, String authorName, UUID parent, boolean deleted) {
    Comment c = new Comment();
    c.setId(id);
    c.setReadingId(reading);
    c.setAuthorId(author);
    c.setAuthorName(authorName);
    c.setParentId(parent);
    c.setContent("Hello world");
    c.setDeleted(deleted);
    c.setCreatedAt(Instant.now());
    return c;
  }

  // ─────────────────────────── getComments ────────────────────────

  @Test
  void getCommentsShouldReturnEmptyListWhenNoComments() {
    when(commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId))
        .thenReturn(Collections.emptyList());

    List<CommentView> result = forumService.getComments(readingId);

    assertTrue(result.isEmpty());
  }

  @Test
  void getCommentsShouldReturnOnlyRootComments() {
    Comment root = buildComment(UUID.randomUUID(), readingId, userId, "Alice", null, false);
    Comment reply =
        buildComment(UUID.randomUUID(), readingId, userId, "Alice", root.getId(), false);

    when(commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId))
        .thenReturn(List.of(root, reply));
    when(reactionRepository.findByCommentIdIn(anySet())).thenReturn(Collections.emptyList());

    List<CommentView> result = forumService.getComments(readingId);

    assertEquals(1, result.size(), "Only root comment should be at top level");
    assertEquals(1, result.get(0).replies().size(), "Root should contain 1 reply");
  }

  @Test
  void getCommentsShouldHideContentOfDeletedComments() {
    Comment deleted = buildComment(UUID.randomUUID(), readingId, userId, "Alice", null, true);

    when(commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId))
        .thenReturn(List.of(deleted));
    when(reactionRepository.findByCommentIdIn(anySet())).thenReturn(Collections.emptyList());

    List<CommentView> result = forumService.getComments(readingId);

    assertEquals(1, result.size());
    assertNull(result.get(0).content(), "Deleted comment content should be null");
    assertTrue(result.get(0).deleted());
  }

  @Test
  void getCommentsShouldAggregateReactionCounts() {
    UUID cid = UUID.randomUUID();
    Comment root = buildComment(cid, readingId, userId, "Alice", null, false);

    Reaction r1 = new Reaction();
    r1.setId(UUID.randomUUID());
    r1.setCommentId(cid);
    r1.setUserId(UUID.randomUUID());
    r1.setType(ReactionType.UPVOTE);
    r1.setCreatedAt(Instant.now());
    Reaction r2 = new Reaction();
    r2.setId(UUID.randomUUID());
    r2.setCommentId(cid);
    r2.setUserId(UUID.randomUUID());
    r2.setType(ReactionType.UPVOTE);
    r2.setCreatedAt(Instant.now());

    when(commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId)).thenReturn(List.of(root));
    when(reactionRepository.findByCommentIdIn(anySet())).thenReturn(List.of(r1, r2));

    List<CommentView> result = forumService.getComments(readingId);

    assertEquals(2L, result.get(0).reactionCounts().get(ReactionType.UPVOTE));
  }

  // ─────────────────────────── postComment ────────────────────────

  @Test
  void postCommentShouldSaveAndReturnView() {
    when(commentRepository.save(any(Comment.class)))
        .thenAnswer(
            inv -> {
              Comment c = inv.getArgument(0);
              if (c.getId() == null) c.setId(UUID.randomUUID());
              return c;
            });

    CommentView view = forumService.postComment(readingId, userId, "Alice", "Great article!");

    assertNotNull(view);
    assertEquals("Great article!", view.content());
    assertEquals("Alice", view.authorName());
    assertFalse(view.deleted());
    assertNull(view.parentId());
    verify(commentRepository).save(any(Comment.class));
    verify(eventPublisher).publishCommentCreated(any(), any(), any());
  }

  @Test
  void postCommentShouldThrowWhenContentIsBlank() {
    assertThrows(
        ForumBadRequestException.class,
        () -> forumService.postComment(readingId, userId, "Alice", "   "));
  }

  @Test
  void postCommentShouldThrowWhenContentIsNull() {
    assertThrows(
        ForumBadRequestException.class,
        () -> forumService.postComment(readingId, userId, "Alice", null));
  }

  @Test
  void postCommentShouldThrowWhenContentExceedsMaxLength() {
    String tooLong = "a".repeat(2001);
    assertThrows(
        ForumBadRequestException.class,
        () -> forumService.postComment(readingId, userId, "Alice", tooLong));
  }

  @Test
  void postCommentShouldAcceptContentAtMaxLength() {
    String maxContent = "a".repeat(2000);
    when(commentRepository.save(any(Comment.class)))
        .thenAnswer(
            inv -> {
              Comment c = inv.getArgument(0);
              if (c.getId() == null) c.setId(UUID.randomUUID());
              return c;
            });

    assertDoesNotThrow(() -> forumService.postComment(readingId, userId, "Alice", maxContent));
  }

  // ─────────────────────────── replyToComment ─────────────────────

  @Test
  void replyToCommentShouldSaveReplyWithParentId() {
    Comment parent = buildComment(commentId, readingId, userId, "Alice", null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(parent));
    when(commentRepository.save(any(Comment.class)))
        .thenAnswer(
            inv -> {
              Comment c = inv.getArgument(0);
              if (c.getId() == null) c.setId(UUID.randomUUID());
              return c;
            });

    UUID replierId = UUID.randomUUID();
    CommentView view = forumService.replyToComment(commentId, replierId, "Replier", "I agree!");

    assertNotNull(view);
    assertEquals(commentId, view.parentId());

    ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
    verify(commentRepository).save(captor.capture());
    assertEquals(commentId, captor.getValue().getParentId());
    verify(eventPublisher).publishCommentCreated(any(), any(), any());
  }

  @Test
  void replyToCommentShouldThrowWhenParentNotFound() {
    when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

    assertThrows(
        ForumNotFoundException.class,
        () -> forumService.replyToComment(commentId, userId, "Alice", "reply"));
  }

  @Test
  void replyToCommentShouldThrowWhenParentIsDeleted() {
    Comment deleted = buildComment(commentId, readingId, userId, "Alice", null, true);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(deleted));

    assertThrows(
        ForumBadRequestException.class,
        () -> forumService.replyToComment(commentId, userId, "Alice", "reply"));
  }

  @Test
  void replyToCommentShouldThrowWhenContentIsBlank() {
    assertThrows(
        ForumBadRequestException.class,
        () -> forumService.replyToComment(commentId, userId, "Alice", ""));

    verify(commentRepository, never()).findById(any());
    verify(commentRepository, never()).save(any());
  }

  // ─────────────────────────── editComment ────────────────────────

  @Test
  void editCommentShouldUpdateContentAndEditedAt() {
    Comment comment = buildComment(commentId, readingId, userId, "Alice", null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    forumService.editComment(commentId, userId, "Updated content");

    ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
    verify(commentRepository).save(captor.capture());
    assertEquals("Updated content", captor.getValue().getContent());
    assertNotNull(captor.getValue().getEditedAt());
  }

  @Test
  void editCommentShouldThrowWhenCommentNotFound() {
    when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

    assertThrows(
        ForumNotFoundException.class, () -> forumService.editComment(commentId, userId, "new"));
  }

  @Test
  void editCommentShouldThrowWhenCommentIsDeleted() {
    Comment deleted = buildComment(commentId, readingId, userId, "Alice", null, true);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(deleted));

    assertThrows(
        ForumBadRequestException.class, () -> forumService.editComment(commentId, userId, "new"));
  }

  @Test
  void editCommentShouldThrowWhenRequesterIsNotAuthor() {
    UUID anotherUser = UUID.randomUUID();
    Comment comment = buildComment(commentId, readingId, anotherUser, "Bob", null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    assertThrows(
        ForumForbiddenException.class, () -> forumService.editComment(commentId, userId, "new"));
  }

  @Test
  void editCommentShouldThrowWhenNewContentIsBlank() {
    assertThrows(
        ForumBadRequestException.class, () -> forumService.editComment(commentId, userId, ""));

    verify(commentRepository, never()).findById(any());
    verify(commentRepository, never()).save(any());
  }

  // ─────────────────────────── deleteComment ──────────────────────

  @Test
  void deleteCommentShouldSoftDeleteWhenAuthorRequests() {
    Comment comment = buildComment(commentId, readingId, userId, "Alice", null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    forumService.deleteComment(commentId, userId, false);

    ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
    verify(commentRepository).save(captor.capture());
    assertTrue(captor.getValue().isDeleted());
    verify(eventPublisher).publishCommentDeleted(any(), any(), anyBoolean());
  }

  @Test
  void deleteCommentShouldSoftDeleteWhenAdminRequests() {
    UUID anotherUser = UUID.randomUUID();
    Comment comment = buildComment(commentId, readingId, anotherUser, "Bob", null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    forumService.deleteComment(commentId, userId, true);

    ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
    verify(commentRepository).save(captor.capture());
    assertTrue(captor.getValue().isDeleted());
    verify(eventPublisher).publishCommentDeleted(any(), any(), anyBoolean());
  }

  @Test
  void deleteCommentShouldThrowWhenRequesterIsNotAuthorAndNotAdmin() {
    UUID anotherUser = UUID.randomUUID();
    Comment comment = buildComment(commentId, readingId, anotherUser, "Bob", null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    assertThrows(
        ForumForbiddenException.class, () -> forumService.deleteComment(commentId, userId, false));
  }

  @Test
  void deleteCommentShouldThrowWhenCommentNotFound() {
    when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

    assertThrows(
        ForumNotFoundException.class, () -> forumService.deleteComment(commentId, userId, false));
  }

  @Test
  void deleteCommentShouldBeIdempotentWhenAlreadyDeleted() {
    Comment deleted = buildComment(commentId, readingId, userId, "Alice", null, true);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(deleted));

    // Should not throw, and should not call save again
    assertDoesNotThrow(() -> forumService.deleteComment(commentId, userId, false));
    verify(commentRepository, never()).save(any());
  }

  // ─────────────────────────── toggleReaction ─────────────────────

  @Test
  void toggleReactionShouldAddReactionWhenNotExists() {
    Comment comment = buildComment(commentId, readingId, userId, "Alice", null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
    when(reactionRepository.existsByCommentIdAndUserIdAndType(
            commentId, userId, ReactionType.UPVOTE))
        .thenReturn(false);

    forumService.toggleReaction(commentId, userId, ReactionType.UPVOTE);

    verify(reactionRepository).save(any(Reaction.class));
  }

  @Test
  void toggleReactionShouldRemoveReactionWhenAlreadyExists() {
    Comment comment = buildComment(commentId, readingId, userId, "Alice", null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
    when(reactionRepository.existsByCommentIdAndUserIdAndType(
            commentId, userId, ReactionType.UPVOTE))
        .thenReturn(true);

    forumService.toggleReaction(commentId, userId, ReactionType.UPVOTE);

    verify(reactionRepository)
        .deleteByCommentIdAndUserIdAndType(commentId, userId, ReactionType.UPVOTE);
    verify(reactionRepository, never()).save(any());
  }

  @Test
  void toggleReactionShouldRemoveOppositeVoteBeforeAddingUpvote() {
    Comment comment = buildComment(commentId, readingId, userId, "Alice", null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
    when(reactionRepository.existsByCommentIdAndUserIdAndType(
            commentId, userId, ReactionType.UPVOTE))
        .thenReturn(false);

    forumService.toggleReaction(commentId, userId, ReactionType.UPVOTE);

    verify(reactionRepository)
        .deleteByCommentIdAndUserIdAndTypeIn(
            eq(commentId), eq(userId), argThat(types -> types.contains(ReactionType.DOWNVOTE)));
    verify(reactionRepository).save(any(Reaction.class));
  }

  @Test
  void toggleReactionShouldRemoveOppositeVoteBeforeAddingDownvote() {
    Comment comment = buildComment(commentId, readingId, userId, "Alice", null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
    when(reactionRepository.existsByCommentIdAndUserIdAndType(
            commentId, userId, ReactionType.DOWNVOTE))
        .thenReturn(false);

    forumService.toggleReaction(commentId, userId, ReactionType.DOWNVOTE);

    verify(reactionRepository)
        .deleteByCommentIdAndUserIdAndTypeIn(
            eq(commentId), eq(userId), argThat(types -> types.contains(ReactionType.UPVOTE)));
    verify(reactionRepository).save(any(Reaction.class));
  }

  @Test
  void toggleReactionShouldNotRemoveOppositeForEmojiReaction() {
    Comment comment = buildComment(commentId, readingId, userId, "Alice", null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
    when(reactionRepository.existsByCommentIdAndUserIdAndType(
            commentId, userId, ReactionType.EMOJI_LIKE))
        .thenReturn(false);

    forumService.toggleReaction(commentId, userId, ReactionType.EMOJI_LIKE);

    verify(reactionRepository, never()).deleteByCommentIdAndUserIdAndTypeIn(any(), any(), any());
    verify(reactionRepository).save(any(Reaction.class));
  }

  @Test
  void toggleReactionShouldThrowWhenCommentNotFound() {
    when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

    assertThrows(
        ForumNotFoundException.class,
        () -> forumService.toggleReaction(commentId, userId, ReactionType.UPVOTE));
  }

  @Test
  void toggleReactionShouldThrowWhenCommentIsDeleted() {
    Comment deleted = buildComment(commentId, readingId, userId, "Alice", null, true);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(deleted));

    assertThrows(
        ForumBadRequestException.class,
        () -> forumService.toggleReaction(commentId, userId, ReactionType.UPVOTE));
  }
}
