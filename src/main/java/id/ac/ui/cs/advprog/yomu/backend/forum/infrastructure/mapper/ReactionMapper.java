package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure.mapper;

import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Reaction;
import id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure.persistence.ReactionEntity;

public final class ReactionMapper {

  private ReactionMapper() {}

  public static Reaction toDomain(ReactionEntity entity) {
    Reaction reaction = new Reaction();
    reaction.setId(entity.getId());
    reaction.setCommentId(entity.getCommentId());
    reaction.setUserId(entity.getUserId());
    reaction.setType(entity.getType());
    reaction.setCreatedAt(entity.getCreatedAt());
    return reaction;
  }

  public static ReactionEntity toEntity(Reaction reaction) {
    ReactionEntity entity = new ReactionEntity();
    entity.setId(reaction.getId());
    entity.setCommentId(reaction.getCommentId());
    entity.setUserId(reaction.getUserId());
    entity.setType(reaction.getType());
    entity.setCreatedAt(reaction.getCreatedAt());
    return entity;
  }
}
