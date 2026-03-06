package id.ac.ui.cs.advprog.yomu.backend.repository;

import id.ac.ui.cs.advprog.yomu.backend.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {

    boolean existsByStudentIdAndReadingId(String studentId, UUID readingId);

    List<QuizAttempt> findByStudentId(String studentId);
}
