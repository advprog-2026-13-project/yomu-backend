package id.ac.ui.cs.advprog.yomu.backend.forum.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ReactionTest {

  @Test
  void defaultFieldsShouldBeNull() {
    Reaction r = new Reaction();
    assertNull(r.getId());
    assertNull(r.getCommentId());
    assertNull(r.getUserId());
    assertNull(r.getType());
    assertNull(r.getCreatedAt());
  }

  @Test
  void setterAndGettersShouldWork() {
    UUID id = UUID.randomUUID();
    UUID commentId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();

    Reaction r = new Reaction();
    r.setId(id);
    r.setCommentId(commentId);
    r.setUserId(userId);
    r.setType(ReactionType.UPVOTE);
    r.setCreatedAt(now);

    assertEquals(id, r.getId());
    assertEquals(commentId, r.getCommentId());
    assertEquals(userId, r.getUserId());
    assertEquals(ReactionType.UPVOTE, r.getType());
    assertEquals(now, r.getCreatedAt());
  }

  @Test
  void allArgsConstructorShouldSetAllFields() {
    UUID id = UUID.randomUUID();
    UUID commentId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();

    Reaction r = new Reaction(id, commentId, userId, ReactionType.EMOJI_LIKE, now);

    assertEquals(id, r.getId());
    assertEquals(commentId, r.getCommentId());
    assertEquals(userId, r.getUserId());
    assertEquals(ReactionType.EMOJI_LIKE, r.getType());
    assertEquals(now, r.getCreatedAt());
  }

  @Test
  void eachReactionTypeShouldBeSettable() {
    for (ReactionType type : ReactionType.values()) {
      Reaction r = new Reaction();
      r.setType(type);
      assertEquals(type, r.getType());
    }
  }
}
