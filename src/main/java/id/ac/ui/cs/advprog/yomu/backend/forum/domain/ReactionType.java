package id.ac.ui.cs.advprog.yomu.backend.forum.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Locale;

public enum ReactionType {
  UPVOTE,
  DOWNVOTE,
  EMOJI_LIKE,
  EMOJI_LAUGH,
  EMOJI_WOW,
  EMOJI_SAD,
  EMOJI_ANGRY;

  public boolean isVote() {
    return this == UPVOTE || this == DOWNVOTE;
  }

  @JsonCreator
  public static ReactionType fromWire(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Reaction type must not be empty");
    }

    try {
      return ReactionType.valueOf(value.trim().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid reaction type: " + value);
    }
  }
}
