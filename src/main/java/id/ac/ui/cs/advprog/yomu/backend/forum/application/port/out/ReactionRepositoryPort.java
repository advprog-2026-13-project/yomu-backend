package id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out;

import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Reaction;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ReactionRepositoryPort {
  boolean existsByCommentIdAndUserIdAndType(UUID commentId, UUID userId, ReactionType type);

  void deleteByCommentIdAndUserIdAndType(UUID commentId, UUID userId, ReactionType type);

  void deleteByCommentIdAndUserIdAndTypeIn(
      UUID commentId, UUID userId, Collection<ReactionType> types);

  List<Reaction> findByCommentIdIn(Collection<UUID> commentIds);

  Reaction save(Reaction reaction);
}
