package id.ac.ui.cs.advprog.yomu.backend.reading.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.reading.domain.Question;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
  @EntityGraph(attributePaths = {"options"})
  List<Question> findByReading_ReadingId(UUID readingId);
}
