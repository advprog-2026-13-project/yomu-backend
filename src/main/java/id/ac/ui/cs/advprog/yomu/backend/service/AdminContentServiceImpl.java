package id.ac.ui.cs.advprog.yomu.backend.service;

import id.ac.ui.cs.advprog.yomu.backend.dto.QuestionDTO;
import id.ac.ui.cs.advprog.yomu.backend.dto.ReadingDTO;
import id.ac.ui.cs.advprog.yomu.backend.model.Question;
import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import id.ac.ui.cs.advprog.yomu.backend.repository.QuestionRepository;
import id.ac.ui.cs.advprog.yomu.backend.repository.ReadingRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminContentServiceImpl implements AdminContentService {

  private final ReadingRepository readingRepository;
  private final QuestionRepository questionRepository;

  @Override
  @Transactional
  public Reading createReading(ReadingDTO readingDto) {
    Reading reading = new Reading();
    reading.setTitle(readingDto.title());
    reading.setContent(readingDto.content());
    // Ensure this matches your Reading model fields (author/category)
    // reading.setAuthor(readingDto.author());

    return readingRepository.save(reading);
  }

  @Override
  @Transactional(readOnly = true)
  public Reading getReadingById(UUID readingId) {
    return readingRepository
            .findById(readingId)
            .orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Reading dengan ID " + readingId + " tidak ditemukan."));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Reading> getAllReadings() {
    return readingRepository.findAll();
  }

  @Override
  @Transactional
  public Reading updateReading(UUID id, ReadingDTO updatedReadingDto) {
    // Calling getReadingById here is fine because it's public and correctly proxied
    Reading existingReading = getReadingById(id);

    existingReading.setTitle(updatedReadingDto.title());
    existingReading.setContent(updatedReadingDto.content());
    // existingReading.setAuthor(updatedReadingDto.author());

    return readingRepository.save(existingReading);
  }

  @Override
  @Transactional
  public void deleteReading(UUID readingId) {
    if (!readingRepository.existsById(readingId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID tidak ditemukan");
    }
    readingRepository.deleteById(readingId);
  }

  @Override
  @Transactional
  public Question addQuestionToReading(UUID readingId, QuestionDTO questionDto) {
    Reading reading = getReadingById(readingId);

    Question question = new Question();
    question.setQuestionText(questionDto.questionText());
    question.setOptions(questionDto.options());
    question.setCorrectAnswer(questionDto.correctAnswer());
    question.setReading(reading);

    return questionRepository.save(question);
  }

  @Override
  @Transactional
  public Question updateQuestion(UUID questionId, QuestionDTO updatedQuestionDto) {
    Question existingQuestion = questionRepository.findById(questionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Question tidak ditemukan."));

    existingQuestion.setQuestionText(updatedQuestionDto.questionText());
    existingQuestion.setOptions(updatedQuestionDto.options());
    existingQuestion.setCorrectAnswer(updatedQuestionDto.correctAnswer());

    return questionRepository.save(existingQuestion);
  }

  @Override
  @Transactional
  public void deleteQuestion(UUID questionId) {
    questionRepository.deleteById(questionId);
  }
}