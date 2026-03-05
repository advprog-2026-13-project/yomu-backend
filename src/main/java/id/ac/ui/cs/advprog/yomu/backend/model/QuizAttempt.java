package id.ac.ui.cs.advprog.yomu.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter @Setter
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID quizAttemptId;

    private String studentId;

    private UUID readingId;

    private Integer score;

    private LocalDateTime completedAt;
}
