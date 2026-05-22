package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.CommentRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Comment;
import id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure.mapper.CommentMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ForumCommentRepositoryAdapter implements CommentRepositoryPort {

  private final CommentRepository commentRepository;

  public ForumCommentRepositoryAdapter(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  @Override
  public List<Comment> findByReadingIdOrderByCreatedAtAsc(UUID readingId) {
    return commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId).stream()
        .map(CommentMapper::toDomain)
        .toList();
  }

  @Override
  public Optional<Comment> findById(UUID id) {
    return commentRepository.findById(id).map(CommentMapper::toDomain);
  }

  @Override
  public Comment save(Comment comment) {
    return CommentMapper.toDomain(commentRepository.save(CommentMapper.toEntity(comment)));
  }
}
