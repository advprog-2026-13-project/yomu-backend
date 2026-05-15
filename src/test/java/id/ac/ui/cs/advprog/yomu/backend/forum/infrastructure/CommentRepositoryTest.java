package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure.persistence.CommentEntity;
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

    CommentEntity c1 = new CommentEntity();
    c1.setReadingId(readingId);
    c1.setAuthorId(authorId);
    c1.setContent("First");
    c1.setCreatedAt(Instant.now().minusSeconds(100));
    commentRepository.save(c1);

    CommentEntity c2 = new CommentEntity();
    c2.setReadingId(readingId);
    c2.setAuthorId(authorId);
    c2.setContent("Second");
    c2.setCreatedAt(Instant.now());
    commentRepository.save(c2);

    // Another readingId
    CommentEntity c3 = new CommentEntity();
    c3.setReadingId(UUID.randomUUID());
    c3.setAuthorId(authorId);
    c3.setContent("Other");
    commentRepository.save(c3);

    List<CommentEntity> results = commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId);

    assertEquals(2, results.size());
    assertEquals("First", results.get(0).getContent());
    assertEquals("Second", results.get(1).getContent());
  }

  @Test
  void saveShouldWorkWithUUIDGeneration() {
    CommentEntity c = new CommentEntity();
    c.setReadingId(UUID.randomUUID());
    c.setAuthorId(UUID.randomUUID());
    c.setContent("Test");
    c.setCreatedAt(Instant.now());

    CommentEntity saved = commentRepository.save(c);

    assertNotNull(saved.getId());
    assertEquals("Test", saved.getContent());
  }
}
