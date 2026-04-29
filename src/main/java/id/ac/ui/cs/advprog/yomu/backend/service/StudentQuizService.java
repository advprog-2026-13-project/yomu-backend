package id.ac.ui.cs.advprog.yomu.backend.service;

import id.ac.ui.cs.advprog.yomu.backend.dto.QuestionResponse;
import id.ac.ui.cs.advprog.yomu.backend.dto.QuizSubmissionRequest;
import id.ac.ui.cs.advprog.yomu.backend.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import java.util.List;
import java.util.UUID;

public interface StudentQuizService {
  List<Reading> getAvailableReadingsForStudent(String studentId);

  Reading getReadingForStudent(String studentId, UUID readingId);

  List<QuestionResponse> getQuestionsForReading(String studentId, UUID readingId);

  QuizAttempt submitQuiz(UUID readingId, QuizSubmissionRequest request);
}
