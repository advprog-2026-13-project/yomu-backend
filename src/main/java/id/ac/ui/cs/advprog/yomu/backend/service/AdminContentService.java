package id.ac.ui.cs.advprog.yomu.backend.service;

import id.ac.ui.cs.advprog.yomu.backend.dto.QuestionDTO;
import id.ac.ui.cs.advprog.yomu.backend.dto.ReadingDTO;
import id.ac.ui.cs.advprog.yomu.backend.model.Question;
import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import java.util.List;
import java.util.UUID;

public interface AdminContentService {
  // Use DTO for Creation
  Reading createReading(ReadingDTO readingDto);

  Reading getReadingById(UUID readingId);

  List<Reading> getAllReadings();

  // Use DTO for Updates
  Reading updateReading(UUID id, ReadingDTO updatedReadingDto);

  void deleteReading(UUID readingId);

  // Use DTO for Questions
  Question addQuestionToReading(UUID readingId, QuestionDTO questionDto);

  Question updateQuestion(UUID questionId, QuestionDTO updatedQuestionDto);

  void deleteQuestion(UUID questionId);
}
