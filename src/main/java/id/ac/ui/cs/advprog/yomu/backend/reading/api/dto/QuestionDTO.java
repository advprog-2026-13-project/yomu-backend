package id.ac.ui.cs.advprog.yomu.backend.reading.api.dto;

import java.util.List;

public record QuestionDTO(String questionText, List<String> options, String correctAnswer) {}
