package id.ac.ui.cs.advprog.yomu.backend.reading.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.reading.domain.Reading;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, UUID> {}
