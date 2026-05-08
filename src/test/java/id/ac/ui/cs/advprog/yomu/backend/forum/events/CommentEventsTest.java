package id.ac.ui.cs.advprog.yomu.backend.forum.events;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CommentEventsTest {

  @Test
  void commentCreatedEventShouldBeInstantiable() {
    CommentCreatedEvent event = new CommentCreatedEvent();
    assertNotNull(event);
  }

  @Test
  void commentDeletedEventShouldBeInstantiable() {
    CommentDeletedEvent event = new CommentDeletedEvent();
    assertNotNull(event);
  }
}
