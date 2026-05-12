package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.CommentRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Comment;
import id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure.persistence.CommentEntity;
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
        .map(this::toDomain)
        .toList();
  }

  @Override
  public Optional<Comment> findById(UUID id) {
    return commentRepository.findById(id).map(this::toDomain);
  }

  @Override
  public Comment save(Comment comment) {
    CommentEntity saved = commentRepository.save(toEntity(comment));
    return toDomain(saved);
  }

  private Comment toDomain(CommentEntity entity) {
    Comment comment = new Comment();
    comment.setId(entity.getId());
    comment.setReadingId(entity.getReadingId());
    comment.setAuthorId(entity.getAuthorId());
    comment.setParentId(entity.getParentId());
    comment.setContent(entity.getContent());
    comment.setDeleted(entity.isDeleted());
    comment.setCreatedAt(entity.getCreatedAt());
    comment.setEditedAt(entity.getEditedAt());
    return comment;
  }

  private CommentEntity toEntity(Comment comment) {
    CommentEntity entity = new CommentEntity();
    entity.setId(comment.getId());
    entity.setReadingId(comment.getReadingId());
    entity.setAuthorId(comment.getAuthorId());
    entity.setParentId(comment.getParentId());
    entity.setContent(comment.getContent());
    entity.setDeleted(comment.isDeleted());
    entity.setCreatedAt(comment.getCreatedAt());
    entity.setEditedAt(comment.getEditedAt());
    return entity;
  }
}
