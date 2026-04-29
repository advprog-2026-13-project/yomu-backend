package id.ac.ui.cs.advprog.yomu.backend.repository;

import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, UUID> {
  // Basic CRUD sudah otomatis tersedia
}
