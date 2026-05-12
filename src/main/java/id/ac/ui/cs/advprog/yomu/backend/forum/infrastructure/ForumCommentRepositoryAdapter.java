package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.CommentRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Comment;

@Component
public class ForumCommentRepositoryAdapter implements CommentRepositoryPort {

  private final CommentRepository commentRepository;

  public ForumCommentRepositoryAdapter(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  @Override
  public List<Comment> findByReadingIdOrderByCreatedAtAsc(UUID readingId) {
    return commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId);
  }

  @Override
  public Optional<Comment> findById(UUID id) {
    return commentRepository.findById(id);
  }

  @Override
  public Comment save(Comment comment) {
    return commentRepository.save(comment);
  }
}
