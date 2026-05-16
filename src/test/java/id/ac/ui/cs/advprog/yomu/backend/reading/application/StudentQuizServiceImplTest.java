package id.ac.ui.cs.advprog.yomu.backend.reading.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.reading.api.dto.QuestionResponse;
import id.ac.ui.cs.advprog.yomu.backend.reading.api.dto.QuizSubmissionRequest;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.Question;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.Reading;
import id.ac.ui.cs.advprog.yomu.backend.reading.infrastructure.QuestionRepository;
import id.ac.ui.cs.advprog.yomu.backend.reading.infrastructure.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomu.backend.reading.infrastructure.ReadingRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentQuizServiceImplTest {

  @Mock private ReadingRepository readingRepository;
  @Mock private QuestionRepository questionRepository;
  @Mock private QuizAttemptRepository quizAttemptRepository;
  @Mock private org.springframework.context.ApplicationEventPublisher eventPublisher;

  private StudentQuizServiceImpl studentQuizService;

  private UUID userId;
  private UUID readingId;
  private Reading reading;

  @BeforeEach
  void setUp() {
    studentQuizService = new StudentQuizServiceImpl(
        readingRepository, questionRepository, quizAttemptRepository, eventPublisher
    );
    userId = UUID.randomUUID();
    readingId = UUID.randomUUID();
    reading = new Reading();
    reading.setReadingId(readingId);
  }

  // --- 1. Tests for getAvailableReadingsForStudent ---

  @Test
  void testGetAvailableReadings_FiltersAttemptedReadings() {
    Reading reading2 = new Reading();
    reading2.setReadingId(UUID.randomUUID());

    when(readingRepository.findAll()).thenReturn(List.of(reading, reading2));
    when(quizAttemptRepository.existsByStudentIdAndReadingId(userId.toString(), readingId))
        .thenReturn(true);
    when(quizAttemptRepository.existsByStudentIdAndReadingId(
            userId.toString(), reading2.getReadingId()))
        .thenReturn(false);

    List<Reading> available = studentQuizService.getAvailableReadingsForStudent(userId);

    assertThat(available).hasSize(1);
    assertThat(available.get(0).getReadingId()).isEqualTo(reading2.getReadingId());
  }

  // --- 2. Tests for getReadingForStudent ---

  @Test
  void testGetReading_Success() {
    when(quizAttemptRepository.existsByStudentIdAndReadingId(userId.toString(), readingId))
        .thenReturn(false);
    when(readingRepository.findById(readingId)).thenReturn(Optional.of(reading));

    Reading result = studentQuizService.getReadingForStudent(userId, readingId);
    assertThat(result).isNotNull();
  }

  @Test
  void testGetReading_NotFound_ThrowsException() {
    when(quizAttemptRepository.existsByStudentIdAndReadingId(userId.toString(), readingId))
        .thenReturn(false);
    when(readingRepository.findById(readingId)).thenReturn(Optional.empty());

    assertThrows(
        RuntimeException.class, () -> studentQuizService.getReadingForStudent(userId, readingId));
  }

  // --- 3. Tests for getQuestionsForReading ---

  @Test
  void testGetQuestions_AlreadyAttempted_ThrowsException() {
    when(quizAttemptRepository.existsByStudentIdAndReadingId(userId.toString(), readingId))
        .thenReturn(true);

    assertThrows(
        RuntimeException.class, () -> studentQuizService.getQuestionsForReading(userId, readingId));
  }

  @Test
  void testGetQuestions_Success() {
    Question q = new Question();
    q.setQuestionId(UUID.randomUUID());
    q.setQuestionText("What is Java?");
    q.setOptions(List.of("Coffee", "Code"));

    when(quizAttemptRepository.existsByStudentIdAndReadingId(userId.toString(), readingId))
        .thenReturn(false);
    when(questionRepository.findByReading_ReadingId(readingId)).thenReturn(List.of(q));

    List<QuestionResponse> responses = studentQuizService.getQuestionsForReading(userId, readingId);

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).getQuestionText()).isEqualTo("What is Java?");
  }

  // --- 4. Tests for submitQuiz ---

  @Test
  void testSubmitQuiz_NoQuestionsFound_ThrowsException() {
    QuizSubmissionRequest request = new QuizSubmissionRequest();

    when(quizAttemptRepository.existsByStudentIdAndReadingId(userId.toString(), readingId))
        .thenReturn(false);
    when(questionRepository.findByReading_ReadingId(readingId)).thenReturn(new ArrayList<>());

    assertThrows(
        RuntimeException.class, () -> studentQuizService.submitQuiz(userId, readingId, request));
  }

  @Test
  void testSubmitQuiz_CalculatesScoreAndSaves() {
    Question q1 = createQuestion("A");
    Question q2 = createQuestion("B");
    Question q3 = createQuestion("C");

    when(quizAttemptRepository.existsByStudentIdAndReadingId(userId.toString(), readingId))
        .thenReturn(false);
    when(questionRepository.findByReading_ReadingId(readingId)).thenReturn(List.of(q1, q2, q3));

    when(quizAttemptRepository.save(any(QuizAttempt.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    QuizSubmissionRequest.StudentAnswer ans1 = new QuizSubmissionRequest.StudentAnswer();
    ans1.setQuestionId(q1.getQuestionId());
    ans1.setSelectedAnswer("a");

    QuizSubmissionRequest.StudentAnswer ans2 = new QuizSubmissionRequest.StudentAnswer();
    ans2.setQuestionId(q2.getQuestionId());
    ans2.setSelectedAnswer("B");

    QuizSubmissionRequest.StudentAnswer ans3 = new QuizSubmissionRequest.StudentAnswer();
    ans3.setQuestionId(q3.getQuestionId());
    ans3.setSelectedAnswer("Wrong");

    QuizSubmissionRequest request = new QuizSubmissionRequest();
    request.setAnswers(List.of(ans1, ans2, ans3));

    QuizAttempt result = studentQuizService.submitQuiz(userId, readingId, request);

    assertThat(result.getScore()).isEqualTo(67);
    assertThat(result.getStudentId()).isEqualTo(userId.toString());
    verify(quizAttemptRepository, times(1)).save(any());
    verify(eventPublisher, times(2)).publishEvent(any(Object.class));
  }

  private Question createQuestion(String correctAns) {
    Question q = new Question();
    q.setQuestionId(UUID.randomUUID());
    q.setCorrectAnswer(correctAns);
    return q;
  }
}
