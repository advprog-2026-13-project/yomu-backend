package id.ac.ui.cs.advprog.yomu.backend.forum.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CommentTest {

  private Comment buildComment() {
    Comment c = new Comment();
    c.setId(UUID.randomUUID());
    c.setReadingId(UUID.randomUUID());
    c.setAuthorId(UUID.randomUUID());
    c.setParentId(null);
    c.setContent("Some content");
    c.setDeleted(false);
    c.setCreatedAt(Instant.now());
    return c;
  }

  @Test
  void defaultDeletedShouldBeFalse() {
    Comment c = new Comment();
    assertFalse(c.isDeleted());
  }

  @Test
  void setterAndGettersShouldWork() {
    UUID id = UUID.randomUUID();
    UUID readingId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID parentId = UUID.randomUUID();
    Instant now = Instant.now();

    Comment c = new Comment();
    c.setId(id);
    c.setReadingId(readingId);
    c.setAuthorId(authorId);
    c.setParentId(parentId);
    c.setContent("Hello");
    c.setDeleted(true);
    c.setCreatedAt(now);
    c.setEditedAt(now);

    assertEquals(id, c.getId());
    assertEquals(readingId, c.getReadingId());
    assertEquals(authorId, c.getAuthorId());
    assertEquals(parentId, c.getParentId());
    assertEquals("Hello", c.getContent());
    assertTrue(c.isDeleted());
    assertEquals(now, c.getCreatedAt());
    assertEquals(now, c.getEditedAt());
  }

  @Test
  void allArgsConstructorShouldSetAllFields() {
    UUID id = UUID.randomUUID();
    UUID readingId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID parentId = UUID.randomUUID();
    Instant now = Instant.now();

    Comment c = new Comment(id, readingId, authorId, parentId, "Content", false, now, null);

    assertEquals(id, c.getId());
    assertEquals(readingId, c.getReadingId());
    assertEquals(authorId, c.getAuthorId());
    assertEquals(parentId, c.getParentId());
    assertEquals("Content", c.getContent());
    assertFalse(c.isDeleted());
    assertEquals(now, c.getCreatedAt());
    assertNull(c.getEditedAt());
  }

  @Test
  void commentShouldSupportSoftDelete() {
    Comment c = buildComment();
    assertFalse(c.isDeleted());
    c.setDeleted(true);
    assertTrue(c.isDeleted());
  }

  @Test
  void commentWithNullParentIdIsRootComment() {
    Comment c = buildComment();
    assertNull(c.getParentId());
  }

  @Test
  void commentWithParentIdIsReply() {
    UUID parentId = UUID.randomUUID();
    Comment c = buildComment();
    c.setParentId(parentId);
    assertEquals(parentId, c.getParentId());
  }
}
