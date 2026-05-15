package id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out;

import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Comment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepositoryPort {
  List<Comment> findByReadingIdOrderByCreatedAtAsc(UUID readingId);

  Optional<Comment> findById(UUID id);

  Comment save(Comment comment);
}
