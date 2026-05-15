package id.ac.ui.cs.advprog.yomu.backend.reading.api.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class QuizSubmissionRequest {
  private List<StudentAnswer> answers;

  @Data
  public static class StudentAnswer {
    private UUID questionId;
    private String selectedAnswer;
  }
}
