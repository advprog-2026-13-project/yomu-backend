package id.ac.ui.cs.advprog.yomu.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.dto.QuestionResponse;
import id.ac.ui.cs.advprog.yomu.backend.dto.QuizSubmissionRequest;
import id.ac.ui.cs.advprog.yomu.backend.model.Question;
import id.ac.ui.cs.advprog.yomu.backend.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import id.ac.ui.cs.advprog.yomu.backend.repository.QuestionRepository;
import id.ac.ui.cs.advprog.yomu.backend.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomu.backend.repository.ReadingRepository;
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

  @InjectMocks private StudentQuizServiceImpl studentQuizService;

  private String studentId;
  private UUID readingId;
  private Reading reading;

  @BeforeEach
  void setUp() {
    studentId = "student-123";
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
    // First reading is attempted, second is not
    when(quizAttemptRepository.existsByStudentIdAndReadingId(studentId, readingId))
        .thenReturn(true);
    when(quizAttemptRepository.existsByStudentIdAndReadingId(studentId, reading2.getReadingId()))
        .thenReturn(false);

    List<Reading> available = studentQuizService.getAvailableReadingsForStudent(studentId);

    assertThat(available).hasSize(1);
    assertThat(available.get(0).getReadingId()).isEqualTo(reading2.getReadingId());
  }

  // --- 2. Tests for getReadingForStudent ---

  @Test
  void testGetReading_Success() {
    when(quizAttemptRepository.existsByStudentIdAndReadingId(studentId, readingId))
        .thenReturn(false);
    when(readingRepository.findById(readingId)).thenReturn(Optional.of(reading));

    Reading result = studentQuizService.getReadingForStudent(studentId, readingId);
    assertThat(result).isNotNull();
  }

  @Test
  void testGetReading_NotFound_ThrowsException() {
    when(quizAttemptRepository.existsByStudentIdAndReadingId(studentId, readingId))
        .thenReturn(false);
    when(readingRepository.findById(readingId)).thenReturn(Optional.empty());

    assertThrows(
        RuntimeException.class,
        () -> studentQuizService.getReadingForStudent(studentId, readingId));
  }

  // --- 3. Tests for getQuestionsForReading ---

  @Test
  void testGetQuestions_AlreadyAttempted_ThrowsException() {
    when(quizAttemptRepository.existsByStudentIdAndReadingId(studentId, readingId))
        .thenReturn(true);

    assertThrows(
        RuntimeException.class,
        () -> studentQuizService.getQuestionsForReading(studentId, readingId));
  }

  @Test
  void testGetQuestions_Success() {
    Question q = new Question();
    q.setQuestionId(UUID.randomUUID());
    q.setQuestionText("What is Java?");
    q.setOptions(List.of("Coffee", "Code"));

    when(quizAttemptRepository.existsByStudentIdAndReadingId(studentId, readingId))
        .thenReturn(false);
    when(questionRepository.findByReading_ReadingId(readingId)).thenReturn(List.of(q));

    List<QuestionResponse> responses =
        studentQuizService.getQuestionsForReading(studentId, readingId);

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).getQuestionText()).isEqualTo("What is Java?");
  }

  // --- 4. Tests for submitQuiz ---

  @Test
  void testSubmitQuiz_NoQuestionsFound_ThrowsException() {
    QuizSubmissionRequest request = new QuizSubmissionRequest();
    request.setStudentId(studentId);

    when(quizAttemptRepository.existsByStudentIdAndReadingId(studentId, readingId))
        .thenReturn(false);
    when(questionRepository.findByReading_ReadingId(readingId)).thenReturn(new ArrayList<>());

    assertThrows(RuntimeException.class, () -> studentQuizService.submitQuiz(readingId, request));
  }

  @Test
  void testSubmitQuiz_CalculatesScoreAndSaves() {
    // Setup 3 questions
    Question q1 = createQuestion("A");
    Question q2 = createQuestion("B");
    Question q3 = createQuestion("C");

    when(quizAttemptRepository.existsByStudentIdAndReadingId(studentId, readingId))
        .thenReturn(false);
    when(questionRepository.findByReading_ReadingId(readingId)).thenReturn(List.of(q1, q2, q3));

    // Mocking save to return the same object
    when(quizAttemptRepository.save(any(QuizAttempt.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Student gets 2/3 correct (score should be ~67)
    // Student gets 2/3 correct (score should be ~67)
    QuizSubmissionRequest.StudentAnswer ans1 = new QuizSubmissionRequest.StudentAnswer();
    ans1.setQuestionId(q1.getQuestionId());
    ans1.setSelectedAnswer("a"); // correct (case insen)

    QuizSubmissionRequest.StudentAnswer ans2 = new QuizSubmissionRequest.StudentAnswer();
    ans2.setQuestionId(q2.getQuestionId());
    ans2.setSelectedAnswer("B"); // correct

    QuizSubmissionRequest.StudentAnswer ans3 = new QuizSubmissionRequest.StudentAnswer();
    ans3.setQuestionId(q3.getQuestionId());
    ans3.setSelectedAnswer("Wrong"); // incorrect

    QuizSubmissionRequest request = new QuizSubmissionRequest();
    request.setStudentId(studentId);
    request.setAnswers(List.of(ans1, ans2, ans3));
    // QuizSubmissionRequest request = new QuizSubmissionRequest();
    request.setStudentId(studentId);
    request.setAnswers(List.of(ans1, ans2, ans3));

    QuizAttempt result = studentQuizService.submitQuiz(readingId, request);

    assertThat(result.getScore()).isEqualTo(67); // Math.round((2/3)*100)
    assertThat(result.getStudentId()).isEqualTo(studentId);
    verify(quizAttemptRepository, times(1)).save(any());
  }

  // Helper method to create questions
  private Question createQuestion(String correctAns) {
    Question q = new Question();
    q.setQuestionId(UUID.randomUUID());
    q.setCorrectAnswer(correctAns);
    return q;
  }
}
