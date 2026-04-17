package id.ac.ui.cs.advprog.yomu.backend.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class QuestionResponse {
  private UUID questionId;
  private String questionText;
  private List<String> options;
}
