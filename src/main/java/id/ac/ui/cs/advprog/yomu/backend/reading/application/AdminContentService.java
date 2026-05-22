package id.ac.ui.cs.advprog.yomu.backend.reading.application;

import id.ac.ui.cs.advprog.yomu.backend.reading.api.dto.QuestionDTO;
import id.ac.ui.cs.advprog.yomu.backend.reading.api.dto.ReadingDTO;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.Question;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.Reading;
import java.util.List;
import java.util.UUID;

public interface AdminContentService {
  Reading createReading(ReadingDTO readingDto);

  Reading getReadingById(UUID readingId);

  List<Reading> getAllReadings();

  Reading updateReading(UUID id, ReadingDTO updatedReadingDto);

  void deleteReading(UUID readingId);

  void hideReading(UUID readingId);

  void unhideReading(UUID readingId);

  Question addQuestionToReading(UUID readingId, QuestionDTO questionDto);

  List<Question> getQuestionsForReading(UUID readingId);

  Question updateQuestion(UUID questionId, QuestionDTO updatedQuestionDto);

  void deleteQuestion(UUID questionId);
}
