package id.ac.ui.cs.advprog.yomu.backend.reading.application;

import id.ac.ui.cs.advprog.yomu.backend.reading.api.dto.QuestionResponse;
import id.ac.ui.cs.advprog.yomu.backend.reading.api.dto.QuizSubmissionRequest;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.Reading;
import java.util.List;
import java.util.UUID;

public interface StudentQuizService {
  List<Reading> getAllReadingsWithCompletionStatus(UUID userId);

  Reading getReadingForStudent(UUID userId, UUID readingId);

  List<QuestionResponse> getQuestionsForReading(UUID userId, UUID readingId);

  QuizAttempt submitQuiz(UUID userId, UUID readingId, QuizSubmissionRequest request);

  void completeReading(UUID userId, UUID readingId);
}
