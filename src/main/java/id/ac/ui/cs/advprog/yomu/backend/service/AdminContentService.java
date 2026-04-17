package id.ac.ui.cs.advprog.yomu.backend.service;

import id.ac.ui.cs.advprog.yomu.backend.model.Question;
import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import java.util.List;
import java.util.UUID;

public interface AdminContentService {
  Reading createReading(Reading reading);

  Reading getReadingById(UUID readingId);

  List<Reading> getAllReadings();

  Reading updateReading(UUID id, Reading updatedReading);

  void deleteReading(UUID readingId);

  Question addQuestionToReading(UUID readingId, Question question);

  Question updateQuestion(UUID questionId, Question updatedQuestion);

  void deleteQuestion(UUID questionId);
}
