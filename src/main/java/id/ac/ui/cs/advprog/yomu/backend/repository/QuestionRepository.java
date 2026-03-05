package id.ac.ui.cs.advprog.yomu.backend.repository;

import id.ac.ui.cs.advprog.yomu.backend.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    // Gunakan underscore untuk mengakses properti dari objek relasinya
    List<Question> findByReading_ReadingId(UUID readingId);
}