package id.ac.ui.cs.advprog.yomu.backend.repository;

import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, UUID> {
    // Basic CRUD sudah otomatis tersedia
}
