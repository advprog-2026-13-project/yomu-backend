package id.ac.ui.cs.advprog.yomu.backend.forum.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ReactionTypeTest {

  @Test
  void upvoteAndDownvoteShouldBeVotes() {
    assertTrue(ReactionType.UPVOTE.isVote());
    assertTrue(ReactionType.DOWNVOTE.isVote());
  }

  @Test
  void emojiTypesShouldNotBeVotes() {
    assertFalse(ReactionType.EMOJI_LIKE.isVote());
    assertFalse(ReactionType.EMOJI_LAUGH.isVote());
    assertFalse(ReactionType.EMOJI_WOW.isVote());
    assertFalse(ReactionType.EMOJI_SAD.isVote());
    assertFalse(ReactionType.EMOJI_ANGRY.isVote());
  }

  @Test
  void fromWireShouldParseCaseInsensitively() {
    assertEquals(ReactionType.UPVOTE, ReactionType.fromWire("upvote"));
    assertEquals(ReactionType.DOWNVOTE, ReactionType.fromWire("DOWNVOTE"));
    assertEquals(ReactionType.EMOJI_LIKE, ReactionType.fromWire("emoji_like"));
    assertEquals(ReactionType.EMOJI_LAUGH, ReactionType.fromWire("Emoji_Laugh"));
    assertEquals(ReactionType.EMOJI_WOW, ReactionType.fromWire("EMOJI_WOW"));
    assertEquals(ReactionType.EMOJI_SAD, ReactionType.fromWire("emoji_sad"));
    assertEquals(ReactionType.EMOJI_ANGRY, ReactionType.fromWire("EMOJI_ANGRY"));
  }

  @Test
  void fromWireShouldTrimWhitespace() {
    assertEquals(ReactionType.UPVOTE, ReactionType.fromWire("  UPVOTE  "));
  }

  @Test
  void fromWireShouldThrowOnNull() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> ReactionType.fromWire(null));
    assertTrue(ex.getMessage().contains("must not be empty"));
  }

  @Test
  void fromWireShouldThrowOnBlank() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> ReactionType.fromWire("   "));
    assertTrue(ex.getMessage().contains("must not be empty"));
  }

  @Test
  void fromWireShouldThrowOnInvalidValue() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> ReactionType.fromWire("THUMBSUP"));
    assertTrue(ex.getMessage().contains("Invalid reaction type"));
  }
}
