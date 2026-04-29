package id.ac.ui.cs.advprog.yomu.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.dto.QuestionDTO;
import id.ac.ui.cs.advprog.yomu.backend.dto.ReadingDTO;
import id.ac.ui.cs.advprog.yomu.backend.model.Question;
import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import id.ac.ui.cs.advprog.yomu.backend.repository.QuestionRepository;
import id.ac.ui.cs.advprog.yomu.backend.repository.ReadingRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AdminContentServiceImplTest {

  @Mock private ReadingRepository readingRepository;

  @Mock private QuestionRepository questionRepository;

  @InjectMocks private AdminContentServiceImpl service;

  private UUID readingId;
  private UUID questionId;
  private Reading reading;
  private Question question;
  private ReadingDTO readingDto;
  private QuestionDTO questionDto;

  @BeforeEach
  void setUp() {
    readingId = UUID.randomUUID();
    questionId = UUID.randomUUID();

    reading = new Reading();
    reading.setReadingId(readingId);
    reading.setTitle("Title");
    reading.setContent("Content");

    question = new Question();
    question.setQuestionId(questionId);
    question.setQuestionText("Q?");
    question.setOptions(List.of("A", "B"));
    question.setCorrectAnswer("A");

    readingDto = new ReadingDTO("Title", "Content", "Category");
    questionDto = new QuestionDTO("Q?", List.of("A", "B"), "A");
  }

  @Test
  void createReading() {
    when(readingRepository.save(any(Reading.class))).thenReturn(reading);

    Reading result = service.createReading(readingDto);

    assertEquals("Title", result.getTitle());
    verify(readingRepository).save(any(Reading.class));
  }

  @Test
  void getReadingById_success() {
    when(readingRepository.findById(readingId)).thenReturn(Optional.of(reading));

    Reading result = service.getReadingById(readingId);

    assertEquals(readingId, result.getReadingId());
  }

  @Test
  void getReadingById_notFound() {
    when(readingRepository.findById(readingId)).thenReturn(Optional.empty());

    assertThrows(ResponseStatusException.class, () -> service.getReadingById(readingId));
  }

  @Test
  void getAllReadings() {
    when(readingRepository.findAll()).thenReturn(List.of(reading));

    List<Reading> result = service.getAllReadings();

    assertEquals(1, result.size());
  }

  @Test
  void updateReading_success() {
    when(readingRepository.findById(readingId)).thenReturn(Optional.of(reading));
    when(readingRepository.save(any(Reading.class))).thenReturn(reading);

    Reading result = service.updateReading(readingId, readingDto);

    assertEquals("Title", result.getTitle());
    verify(readingRepository).save(reading);
  }

  @Test
  void updateReading_notFound() {
    when(readingRepository.findById(readingId)).thenReturn(Optional.empty());

    assertThrows(ResponseStatusException.class, () -> service.updateReading(readingId, readingDto));
  }

  @Test
  void deleteReading_success() {
    when(readingRepository.existsById(readingId)).thenReturn(true);

    service.deleteReading(readingId);

    verify(readingRepository).deleteById(readingId);
  }

  @Test
  void deleteReading_notFound() {
    when(readingRepository.existsById(readingId)).thenReturn(false);

    assertThrows(ResponseStatusException.class, () -> service.deleteReading(readingId));
  }

  @Test
  void addQuestionToReading() {
    when(readingRepository.findById(readingId)).thenReturn(Optional.of(reading));
    when(questionRepository.save(any(Question.class))).thenReturn(question);

    Question result = service.addQuestionToReading(readingId, questionDto);

    assertEquals("Q?", result.getQuestionText());
    verify(questionRepository).save(any(Question.class));
  }

  @Test
  void updateQuestion_success() {
    when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
    when(questionRepository.save(any(Question.class))).thenReturn(question);

    Question result = service.updateQuestion(questionId, questionDto);

    assertEquals("Q?", result.getQuestionText());
  }

  @Test
  void updateQuestion_notFound() {
    when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

    assertThrows(
        ResponseStatusException.class, () -> service.updateQuestion(questionId, questionDto));
  }

  @Test
  void deleteQuestion() {
    service.deleteQuestion(questionId);

    verify(questionRepository).deleteById(questionId);
  }
}
