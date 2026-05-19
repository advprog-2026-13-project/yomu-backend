package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure.mapper;

import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Comment;
import id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure.persistence.CommentEntity;

public final class CommentMapper {

  private CommentMapper() {}

  public static Comment toDomain(CommentEntity entity) {
    Comment comment = new Comment();
    comment.setId(entity.getId());
    comment.setReadingId(entity.getReadingId());
    comment.setAuthorId(entity.getAuthorId());
    comment.setAuthorName(entity.getAuthorName());
    comment.setParentId(entity.getParentId());
    comment.setContent(entity.getContent());
    comment.setDeleted(entity.isDeleted());
    comment.setCreatedAt(entity.getCreatedAt());
    comment.setEditedAt(entity.getEditedAt());
    return comment;
  }

  public static CommentEntity toEntity(Comment comment) {
    CommentEntity entity = new CommentEntity();
    entity.setId(comment.getId());
    entity.setReadingId(comment.getReadingId());
    entity.setAuthorId(comment.getAuthorId());
    entity.setAuthorName(comment.getAuthorName());
    entity.setParentId(comment.getParentId());
    entity.setContent(comment.getContent());
    entity.setDeleted(comment.isDeleted());
    entity.setCreatedAt(comment.getCreatedAt());
    entity.setEditedAt(comment.getEditedAt());
    return entity;
  }
}
