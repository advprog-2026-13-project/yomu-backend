package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.ReactionRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Reaction;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;
import id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure.persistence.ReactionEntity;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ForumReactionRepositoryAdapter implements ReactionRepositoryPort {

  private final ReactionRepository reactionRepository;

  public ForumReactionRepositoryAdapter(ReactionRepository reactionRepository) {
    this.reactionRepository = reactionRepository;
  }

  @Override
  public boolean existsByCommentIdAndUserIdAndType(UUID commentId, UUID userId, ReactionType type) {
    return reactionRepository.existsByCommentIdAndUserIdAndType(commentId, userId, type);
  }

  @Override
  public void deleteByCommentIdAndUserIdAndType(UUID commentId, UUID userId, ReactionType type) {
    reactionRepository.deleteByCommentIdAndUserIdAndType(commentId, userId, type);
  }

  @Override
  public void deleteByCommentIdAndUserIdAndTypeIn(
      UUID commentId, UUID userId, Collection<ReactionType> types) {
    reactionRepository.deleteByCommentIdAndUserIdAndTypeIn(commentId, userId, types);
  }

  @Override
  public List<Reaction> findByCommentIdIn(Collection<UUID> commentIds) {
    return reactionRepository.findByCommentIdIn(commentIds).stream()
        .map(this::toDomain)
        .toList();
  }

  @Override
  public Reaction save(Reaction reaction) {
    ReactionEntity saved = reactionRepository.save(toEntity(reaction));
    return toDomain(saved);
  }

  private Reaction toDomain(ReactionEntity entity) {
    Reaction reaction = new Reaction();
    reaction.setId(entity.getId());
    reaction.setCommentId(entity.getCommentId());
    reaction.setUserId(entity.getUserId());
    reaction.setType(entity.getType());
    reaction.setCreatedAt(entity.getCreatedAt());
    return reaction;
  }

  private ReactionEntity toEntity(Reaction reaction) {
    ReactionEntity entity = new ReactionEntity();
    entity.setId(reaction.getId());
    entity.setCommentId(reaction.getCommentId());
    entity.setUserId(reaction.getUserId());
    entity.setType(reaction.getType());
    entity.setCreatedAt(reaction.getCreatedAt());
    return entity;
  }
}
