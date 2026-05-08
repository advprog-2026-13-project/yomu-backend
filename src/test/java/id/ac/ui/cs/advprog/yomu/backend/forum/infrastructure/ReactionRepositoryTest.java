package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Reaction;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ReactionRepositoryTest {

  @Autowired private ReactionRepository reactionRepository;

  @Test
  void existsByCommentIdAndUserIdAndTypeShouldReturnCorrectValue() {
    UUID commentId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    ReactionType type = ReactionType.UPVOTE;

    assertFalse(reactionRepository.existsByCommentIdAndUserIdAndType(commentId, userId, type));

    Reaction r = new Reaction();
    r.setCommentId(commentId);
    r.setUserId(userId);
    r.setType(type);
    r.setCreatedAt(Instant.now());
    reactionRepository.save(r);

    assertTrue(reactionRepository.existsByCommentIdAndUserIdAndType(commentId, userId, type));
  }

  @Test
  void deleteByCommentIdAndUserIdAndTypeShouldRemoveCorrectReaction() {
    UUID commentId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    ReactionType type = ReactionType.UPVOTE;

    Reaction r = new Reaction();
    r.setCommentId(commentId);
    r.setUserId(userId);
    r.setType(type);
    r.setCreatedAt(Instant.now());
    reactionRepository.save(r);

    reactionRepository.deleteByCommentIdAndUserIdAndType(commentId, userId, type);
    reactionRepository.flush(); // Ensure deletion is synchronized with DB

    assertFalse(reactionRepository.existsByCommentIdAndUserIdAndType(commentId, userId, type));
  }

  @Test
  void deleteByCommentIdAndUserIdAndTypeInShouldRemoveMultipleTypes() {
    UUID commentId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    Reaction r1 = new Reaction();
    r1.setCommentId(commentId);
    r1.setUserId(userId);
    r1.setType(ReactionType.UPVOTE);
    r1.setCreatedAt(Instant.now());
    reactionRepository.save(r1);

    Reaction r2 = new Reaction();
    r2.setCommentId(commentId);
    r2.setUserId(userId);
    r2.setType(ReactionType.DOWNVOTE);
    r2.setCreatedAt(Instant.now());
    reactionRepository.save(r2);

    reactionRepository.deleteByCommentIdAndUserIdAndTypeIn(
        commentId, userId, List.of(ReactionType.UPVOTE, ReactionType.DOWNVOTE));
    reactionRepository.flush();

    assertFalse(reactionRepository.existsByCommentIdAndUserIdAndType(commentId, userId, ReactionType.UPVOTE));
    assertFalse(reactionRepository.existsByCommentIdAndUserIdAndType(commentId, userId, ReactionType.DOWNVOTE));
  }

  @Test
  void findByCommentIdInShouldReturnAllReactionsForComments() {
    UUID cid1 = UUID.randomUUID();
    UUID cid2 = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    Reaction r1 = new Reaction();
    r1.setCommentId(cid1);
    r1.setUserId(userId);
    r1.setType(ReactionType.EMOJI_LIKE);
    r1.setCreatedAt(Instant.now());
    reactionRepository.save(r1);

    Reaction r2 = new Reaction();
    r2.setCommentId(cid2);
    r2.setUserId(userId);
    r2.setType(ReactionType.EMOJI_WOW);
    r2.setCreatedAt(Instant.now());
    reactionRepository.save(r2);

    List<Reaction> results = reactionRepository.findByCommentIdIn(List.of(cid1, cid2));

    assertEquals(2, results.size());
  }
}
