package id.ac.ui.cs.advprog.yomu.backend.forum.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.dto.CommentView;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Comment;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Reaction;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;
import id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure.CommentRepository;
import id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure.ReactionRepository;

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
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ForumServiceTest {

  @Mock private CommentRepository commentRepository;
  @Mock private ReactionRepository reactionRepository;
  @Mock private UserRepository userRepository;

  @InjectMocks private ForumService forumService;

  private UUID readingId;
  private UUID userId;
  private UUID commentId;
  private User dummyUser;

  @BeforeEach
  void setUp() {
    readingId = UUID.randomUUID();
    userId = UUID.randomUUID();
    commentId = UUID.randomUUID();

    dummyUser = new User("alice", "Alice", "alice@mail.com", "0811", "hashed", Role.USER);
    dummyUser.setId(userId);
  }

  // ─────────────────────────── helpers ────────────────────────────

  private Comment buildComment(UUID id, UUID reading, UUID author, UUID parent, boolean deleted) {
    Comment c = new Comment();
    c.setId(id);
    c.setReadingId(reading);
    c.setAuthorId(author);
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
    Comment root = buildComment(UUID.randomUUID(), readingId, userId, null, false);
    Comment reply = buildComment(UUID.randomUUID(), readingId, userId, root.getId(), false);

    when(commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId))
        .thenReturn(List.of(root, reply));
    when(userRepository.findAllById(anySet())).thenReturn(List.of(dummyUser));
    when(reactionRepository.findByCommentIdIn(anySet())).thenReturn(Collections.emptyList());

    List<CommentView> result = forumService.getComments(readingId);

    assertEquals(1, result.size(), "Only root comment should be at top level");
    assertEquals(1, result.get(0).replies().size(), "Root should contain 1 reply");
  }

  @Test
  void getCommentsShouldHideContentOfDeletedComments() {
    Comment deleted = buildComment(UUID.randomUUID(), readingId, userId, null, true);

    when(commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId))
        .thenReturn(List.of(deleted));
    when(userRepository.findAllById(anySet())).thenReturn(Collections.emptyList());
    when(reactionRepository.findByCommentIdIn(anySet())).thenReturn(Collections.emptyList());

    List<CommentView> result = forumService.getComments(readingId);

    assertEquals(1, result.size());
    assertNull(result.get(0).content(), "Deleted comment content should be null");
    assertTrue(result.get(0).deleted());
  }

  @Test
  void getCommentsShouldAggregateReactionCounts() {
    UUID cid = UUID.randomUUID();
    Comment root = buildComment(cid, readingId, userId, null, false);

    Reaction r1 = new Reaction(); r1.setId(UUID.randomUUID()); r1.setCommentId(cid); r1.setUserId(UUID.randomUUID()); r1.setType(ReactionType.UPVOTE); r1.setCreatedAt(Instant.now());
    Reaction r2 = new Reaction(); r2.setId(UUID.randomUUID()); r2.setCommentId(cid); r2.setUserId(UUID.randomUUID()); r2.setType(ReactionType.UPVOTE); r2.setCreatedAt(Instant.now());

    when(commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId)).thenReturn(List.of(root));
    when(userRepository.findAllById(anySet())).thenReturn(List.of(dummyUser));
    when(reactionRepository.findByCommentIdIn(anySet())).thenReturn(List.of(r1, r2));

    List<CommentView> result = forumService.getComments(readingId);

    assertEquals(2L, result.get(0).reactionCounts().get(ReactionType.UPVOTE));
  }

  // ─────────────────────────── postComment ────────────────────────

  @Test
  void postCommentShouldSaveAndReturnView() {
    when(userRepository.findById(userId)).thenReturn(Optional.of(dummyUser));
    when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
      Comment c = inv.getArgument(0);
      if (c.getId() == null) c.setId(UUID.randomUUID());
      return c;
    });

    CommentView view = forumService.postComment(readingId, userId, "Great article!");

    assertNotNull(view);
    assertEquals("Great article!", view.content());
    assertFalse(view.deleted());
    assertNull(view.parentId());
    verify(commentRepository).save(any(Comment.class));
  }

  @Test
  void postCommentShouldThrowWhenContentIsBlank() {
    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> forumService.postComment(readingId, userId, "   "));
    assertEquals(400, ex.getStatusCode().value());
  }

  @Test
  void postCommentShouldThrowWhenContentIsNull() {
    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> forumService.postComment(readingId, userId, null));
    assertEquals(400, ex.getStatusCode().value());
  }

  @Test
  void postCommentShouldThrowWhenContentExceedsMaxLength() {
    String tooLong = "a".repeat(2001);
    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> forumService.postComment(readingId, userId, tooLong));
    assertEquals(400, ex.getStatusCode().value());
  }

  @Test
  void postCommentShouldAcceptContentAtMaxLength() {
    String maxContent = "a".repeat(2000);
    when(userRepository.findById(userId)).thenReturn(Optional.of(dummyUser));
    when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
      Comment c = inv.getArgument(0);
      if (c.getId() == null) c.setId(UUID.randomUUID());
      return c;
    });

    assertDoesNotThrow(() -> forumService.postComment(readingId, userId, maxContent));
  }

  // ─────────────────────────── replyToComment ─────────────────────

  @Test
  void replyToCommentShouldSaveReplyWithParentId() {
    Comment parent = buildComment(commentId, readingId, userId, null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(parent));
    when(userRepository.findById(userId)).thenReturn(Optional.of(dummyUser));
    when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
      Comment c = inv.getArgument(0);
      if (c.getId() == null) c.setId(UUID.randomUUID());
      return c;
    });

    UUID replierId = UUID.randomUUID();
    CommentView view = forumService.replyToComment(commentId, replierId, "I agree!");

    assertNotNull(view);
    assertEquals(commentId, view.parentId());

    ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
    verify(commentRepository).save(captor.capture());
    assertEquals(commentId, captor.getValue().getParentId());
  }

  @Test
  void replyToCommentShouldThrowWhenParentNotFound() {
    when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> forumService.replyToComment(commentId, userId, "reply"));
    assertEquals(404, ex.getStatusCode().value());
  }

  @Test
  void replyToCommentShouldThrowWhenParentIsDeleted() {
    Comment deleted = buildComment(commentId, readingId, userId, null, true);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(deleted));

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> forumService.replyToComment(commentId, userId, "reply"));
    assertEquals(400, ex.getStatusCode().value());
  }

  @Test
  void replyToCommentShouldThrowWhenContentIsBlank() {
    Comment parent = buildComment(commentId, readingId, userId, null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(parent));

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> forumService.replyToComment(commentId, userId, ""));
    assertEquals(400, ex.getStatusCode().value());
  }

  // ─────────────────────────── editComment ────────────────────────

  @Test
  void editCommentShouldUpdateContentAndEditedAt() {
    Comment comment = buildComment(commentId, readingId, userId, null, false);
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

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> forumService.editComment(commentId, userId, "new"));
    assertEquals(404, ex.getStatusCode().value());
  }

  @Test
  void editCommentShouldThrowWhenCommentIsDeleted() {
    Comment deleted = buildComment(commentId, readingId, userId, null, true);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(deleted));

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> forumService.editComment(commentId, userId, "new"));
    assertEquals(400, ex.getStatusCode().value());
  }

  @Test
  void editCommentShouldThrowWhenRequesterIsNotAuthor() {
    UUID anotherUser = UUID.randomUUID();
    Comment comment = buildComment(commentId, readingId, anotherUser, null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> forumService.editComment(commentId, userId, "new"));
    assertEquals(403, ex.getStatusCode().value());
  }

  @Test
  void editCommentShouldThrowWhenNewContentIsBlank() {
    Comment comment = buildComment(commentId, readingId, userId, null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> forumService.editComment(commentId, userId, ""));
    assertEquals(400, ex.getStatusCode().value());
  }

  // ─────────────────────────── deleteComment ──────────────────────

  @Test
  void deleteCommentShouldSoftDeleteWhenAuthorRequests() {
    Comment comment = buildComment(commentId, readingId, userId, null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    forumService.deleteComment(commentId, userId, false);

    ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
    verify(commentRepository).save(captor.capture());
    assertTrue(captor.getValue().isDeleted());
  }

  @Test
  void deleteCommentShouldSoftDeleteWhenAdminRequests() {
    UUID anotherUser = UUID.randomUUID();
    Comment comment = buildComment(commentId, readingId, anotherUser, null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    forumService.deleteComment(commentId, userId, true);

    ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
    verify(commentRepository).save(captor.capture());
    assertTrue(captor.getValue().isDeleted());
  }

  @Test
  void deleteCommentShouldThrowWhenRequesterIsNotAuthorAndNotAdmin() {
    UUID anotherUser = UUID.randomUUID();
    Comment comment = buildComment(commentId, readingId, anotherUser, null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> forumService.deleteComment(commentId, userId, false));
    assertEquals(403, ex.getStatusCode().value());
  }

  @Test
  void deleteCommentShouldThrowWhenCommentNotFound() {
    when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> forumService.deleteComment(commentId, userId, false));
    assertEquals(404, ex.getStatusCode().value());
  }

  @Test
  void deleteCommentShouldBeIdempotentWhenAlreadyDeleted() {
    Comment deleted = buildComment(commentId, readingId, userId, null, true);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(deleted));

    // Should not throw, and should not call save again
    assertDoesNotThrow(() -> forumService.deleteComment(commentId, userId, false));
    verify(commentRepository, never()).save(any());
  }

  // ─────────────────────────── toggleReaction ─────────────────────

  @Test
  void toggleReactionShouldAddReactionWhenNotExists() {
    Comment comment = buildComment(commentId, readingId, userId, null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
    when(reactionRepository.existsByCommentIdAndUserIdAndType(commentId, userId, ReactionType.UPVOTE))
        .thenReturn(false);

    forumService.toggleReaction(commentId, userId, ReactionType.UPVOTE);

    verify(reactionRepository).save(any(Reaction.class));
  }

  @Test
  void toggleReactionShouldRemoveReactionWhenAlreadyExists() {
    Comment comment = buildComment(commentId, readingId, userId, null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
    when(reactionRepository.existsByCommentIdAndUserIdAndType(commentId, userId, ReactionType.UPVOTE))
        .thenReturn(true);

    forumService.toggleReaction(commentId, userId, ReactionType.UPVOTE);

    verify(reactionRepository).deleteByCommentIdAndUserIdAndType(commentId, userId, ReactionType.UPVOTE);
    verify(reactionRepository, never()).save(any());
  }

  @Test
  void toggleReactionShouldRemoveOppositeVoteBeforeAddingUpvote() {
    Comment comment = buildComment(commentId, readingId, userId, null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
    when(reactionRepository.existsByCommentIdAndUserIdAndType(commentId, userId, ReactionType.UPVOTE))
        .thenReturn(false);

    forumService.toggleReaction(commentId, userId, ReactionType.UPVOTE);

    verify(reactionRepository).deleteByCommentIdAndUserIdAndTypeIn(
        eq(commentId), eq(userId), argThat(types -> types.contains(ReactionType.DOWNVOTE)));
    verify(reactionRepository).save(any(Reaction.class));
  }

  @Test
  void toggleReactionShouldRemoveOppositeVoteBeforeAddingDownvote() {
    Comment comment = buildComment(commentId, readingId, userId, null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
    when(reactionRepository.existsByCommentIdAndUserIdAndType(commentId, userId, ReactionType.DOWNVOTE))
        .thenReturn(false);

    forumService.toggleReaction(commentId, userId, ReactionType.DOWNVOTE);

    verify(reactionRepository).deleteByCommentIdAndUserIdAndTypeIn(
        eq(commentId), eq(userId), argThat(types -> types.contains(ReactionType.UPVOTE)));
    verify(reactionRepository).save(any(Reaction.class));
  }

  @Test
  void toggleReactionShouldNotRemoveOppositeForEmojiReaction() {
    Comment comment = buildComment(commentId, readingId, userId, null, false);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
    when(reactionRepository.existsByCommentIdAndUserIdAndType(commentId, userId, ReactionType.EMOJI_LIKE))
        .thenReturn(false);

    forumService.toggleReaction(commentId, userId, ReactionType.EMOJI_LIKE);

    verify(reactionRepository, never()).deleteByCommentIdAndUserIdAndTypeIn(any(), any(), any());
    verify(reactionRepository).save(any(Reaction.class));
  }

  @Test
  void toggleReactionShouldThrowWhenCommentNotFound() {
    when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> forumService.toggleReaction(commentId, userId, ReactionType.UPVOTE));
    assertEquals(404, ex.getStatusCode().value());
  }

  @Test
  void toggleReactionShouldThrowWhenCommentIsDeleted() {
    Comment deleted = buildComment(commentId, readingId, userId, null, true);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(deleted));

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> forumService.toggleReaction(commentId, userId, ReactionType.UPVOTE));
    assertEquals(400, ex.getStatusCode().value());
  }
}
