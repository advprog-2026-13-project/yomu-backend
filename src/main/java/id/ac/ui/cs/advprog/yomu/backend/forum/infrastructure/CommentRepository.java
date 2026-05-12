package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
	List<Comment> findByReadingIdOrderByCreatedAtAsc(UUID readingId);
}
