package id.ac.ui.cs.advprog.yomu.backend.reading.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.reading.domain.QuizAttempt;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
  boolean existsByStudentIdAndReadingId(String studentId, UUID readingId);

  List<QuizAttempt> findByStudentId(String studentId);
}
