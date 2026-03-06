package id.ac.ui.cs.advprog.yomu.backend.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class QuestionResponse {
    private UUID questionId;
    private String questionText;
    private List<String> options;
}