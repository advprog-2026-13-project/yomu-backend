package id.ac.ui.cs.advprog.yomu.backend.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class QuizSubmissionRequest {
    private String studentId;
    private List<StudentAnswer> answers;

    @Data
    public static class StudentAnswer {
        private UUID questionId;
        private String selectedAnswer;
    }
}
