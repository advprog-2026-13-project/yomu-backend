package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Reaction;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, UUID> {
  boolean existsByCommentIdAndUserIdAndType(UUID commentId, UUID userId, ReactionType type);

  void deleteByCommentIdAndUserIdAndType(UUID commentId, UUID userId, ReactionType type);

  void deleteByCommentIdAndUserIdAndTypeIn(
      UUID commentId, UUID userId, Collection<ReactionType> types);

  List<Reaction> findByCommentIdIn(Collection<UUID> commentIds);
}
