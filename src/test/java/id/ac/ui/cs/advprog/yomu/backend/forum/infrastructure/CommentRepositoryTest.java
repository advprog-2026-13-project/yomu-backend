package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Comment;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest {

  @Autowired private CommentRepository commentRepository;

  @Test
  void findByReadingIdOrderByCreatedAtAscShouldReturnSortedComments() {
    UUID readingId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    Comment c1 = new Comment();
    c1.setReadingId(readingId);
    c1.setAuthorId(authorId);
    c1.setContent("First");
    c1.setCreatedAt(Instant.now().minusSeconds(100));
    commentRepository.save(c1);

    Comment c2 = new Comment();
    c2.setReadingId(readingId);
    c2.setAuthorId(authorId);
    c2.setContent("Second");
    c2.setCreatedAt(Instant.now());
    commentRepository.save(c2);

    // Another readingId
    Comment c3 = new Comment();
    c3.setReadingId(UUID.randomUUID());
    c3.setAuthorId(authorId);
    c3.setContent("Other");
    commentRepository.save(c3);

    List<Comment> results = commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId);

    assertEquals(2, results.size());
    assertEquals("First", results.get(0).getContent());
    assertEquals("Second", results.get(1).getContent());
  }

  @Test
  void saveShouldWorkWithUUIDGeneration() {
    Comment c = new Comment();
    c.setReadingId(UUID.randomUUID());
    c.setAuthorId(UUID.randomUUID());
    c.setContent("Test");
    c.setCreatedAt(Instant.now());

    Comment saved = commentRepository.save(c);

    assertNotNull(saved.getId());
    assertEquals("Test", saved.getContent());
  }
}
