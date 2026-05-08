package id.ac.ui.cs.advprog.yomu.backend.forum;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.forum.api.dto.EditCommentRequest;
import id.ac.ui.cs.advprog.yomu.backend.forum.api.dto.PostCommentRequest;
import id.ac.ui.cs.advprog.yomu.backend.forum.api.dto.ReactRequest;
import id.ac.ui.cs.advprog.yomu.backend.forum.api.dto.ReplyRequest;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.dto.CommentView;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.dto.UserSummary;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ForumDtoTest {

  @Test
  void userSummaryRecordShouldWork() {
    UUID id = UUID.randomUUID();
    UserSummary summary = new UserSummary(id, "Alice");
    assertEquals(id, summary.id());
    assertEquals("Alice", summary.displayName());
  }

  @Test
  void commentViewRecordShouldWork() {
    UUID id = UUID.randomUUID();
    UUID rid = UUID.randomUUID();
    UserSummary author = new UserSummary(UUID.randomUUID(), "Alice");
    Instant now = Instant.now();

    CommentView view = new CommentView(
        id, rid, author, null, "Hello", false, now, null,
        Map.of(ReactionType.UPVOTE, 5L), Collections.emptyList()
    );

    assertEquals(id, view.id());
    assertEquals("Hello", view.content());
    assertEquals(5L, view.reactionCounts().get(ReactionType.UPVOTE));
    assertTrue(view.replies().isEmpty());
  }

  @Test
  void requestDtosShouldWork() {
    PostCommentRequest post = new PostCommentRequest("Post");
    assertEquals("Post", post.getContent());

    ReplyRequest reply = new ReplyRequest("Reply");
    assertEquals("Reply", reply.getContent());

    EditCommentRequest edit = new EditCommentRequest("Edit");
    assertEquals("Edit", edit.getNewContent());

    ReactRequest react = new ReactRequest("UPVOTE");
    assertEquals("UPVOTE", react.getType());
  }
}
