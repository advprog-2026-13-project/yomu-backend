package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;
import id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure.persistence.ReactionEntity;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends JpaRepository<ReactionEntity, UUID> {
  boolean existsByCommentIdAndUserIdAndType(UUID commentId, UUID userId, ReactionType type);

  void deleteByCommentIdAndUserIdAndType(UUID commentId, UUID userId, ReactionType type);

  void deleteByCommentIdAndUserIdAndTypeIn(
      UUID commentId, UUID userId, Collection<ReactionType> types);

  List<ReactionEntity> findByCommentIdIn(Collection<UUID> commentIds);
}
