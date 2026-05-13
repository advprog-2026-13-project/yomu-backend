package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.ReactionRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Reaction;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;
import id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure.mapper.ReactionMapper;
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
      .map(ReactionMapper::toDomain)
        .toList();
  }

  @Override
  public Reaction save(Reaction reaction) {
    return ReactionMapper.toDomain(reactionRepository.save(ReactionMapper.toEntity(reaction)));
  }
}
