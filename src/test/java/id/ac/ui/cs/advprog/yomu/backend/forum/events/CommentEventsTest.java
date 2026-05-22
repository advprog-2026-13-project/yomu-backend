package id.ac.ui.cs.advprog.yomu.backend.forum.events;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class CommentEventsTest {

  @Test
  void commentCreatedEventShouldStoreFields() {
    UUID cid = UUID.randomUUID();
    UUID aid = UUID.randomUUID();
    UUID rid = UUID.randomUUID();

    CommentCreatedEvent event = new CommentCreatedEvent(cid, aid, rid);

    assertEquals(cid, event.getCommentId());
    assertEquals(aid, event.getAuthorId());
    assertEquals(rid, event.getReadingId());
  }

  @Test
  void commentDeletedEventShouldStoreFields() {
    UUID cid = UUID.randomUUID();
    UUID rid = UUID.randomUUID();

    CommentDeletedEvent event = new CommentDeletedEvent(cid, rid, true);

    assertEquals(cid, event.getCommentId());
    assertEquals(rid, event.getRequesterId());
    assertTrue(event.isAdmin());
  }
}
