package id.ac.ui.cs.advprog.yomu.backend.reading.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import id.ac.ui.cs.advprog.yomu.backend.reading.api.dto.QuestionResponse;
import id.ac.ui.cs.advprog.yomu.backend.reading.api.dto.QuizSubmissionRequest;
import id.ac.ui.cs.advprog.yomu.backend.reading.application.StudentQuizService;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.Reading;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class StudentQuizControllerTest {

  @Mock private StudentQuizService studentQuizService;

  private StudentQuizController controller;

  private UUID userId;
  private UUID readingId;

  @BeforeEach
  void setUp() {
    controller = new StudentQuizController(studentQuizService);
    userId = UUID.randomUUID();
    readingId = UUID.randomUUID();

    // Set up SecurityContext with a mock user
    User user = new User("testuser", "Test User", "test@example.com", null, "hash", Role.USER);
    user.setId(userId);
    SecurityUser securityUser = new SecurityUser(user);
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  // --- GET /api/student/readings ---

  @Test
  void getAvailableReadings_ReturnsOkWithReadings() {
    Reading reading = new Reading();
    reading.setReadingId(readingId);
    reading.setTitle("Test Reading");

    when(studentQuizService.getAllReadingsWithCompletionStatus(userId))
        .thenReturn(List.of(reading));

    ResponseEntity<List<Reading>> response = controller.getAvailableReadings();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(1);
    assertThat(response.getBody().get(0).getTitle()).isEqualTo("Test Reading");
  }

  // --- GET /api/student/readings/{readingId} ---

  @Test
  void getReading_ReturnsOkWithReading() {
    Reading reading = new Reading();
    reading.setReadingId(readingId);

    when(studentQuizService.getReadingForStudent(userId, readingId)).thenReturn(reading);

    ResponseEntity<Reading> response = controller.getReading(readingId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReadingId()).isEqualTo(readingId);
  }

  // --- GET /api/student/readings/{readingId}/questions ---

  @Test
  void getQuestions_ReturnsOkWithQuestions() {
    QuestionResponse qr = new QuestionResponse();
    qr.setQuestionId(UUID.randomUUID());
    qr.setQuestionText("What is Java?");
    qr.setOptions(List.of("A", "B"));

    when(studentQuizService.getQuestionsForReading(userId, readingId)).thenReturn(List.of(qr));

    ResponseEntity<List<QuestionResponse>> response = controller.getQuestions(readingId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(1);
    assertThat(response.getBody().get(0).getQuestionText()).isEqualTo("What is Java?");
  }

  // --- POST /api/student/readings/{readingId}/submit ---

  @Test
  void submitQuiz_ReturnsOkWithQuizAttempt() {
    QuizSubmissionRequest request = new QuizSubmissionRequest();
    QuizAttempt attempt = new QuizAttempt();
    attempt.setStudentId(userId);
    attempt.setReadingId(readingId);
    attempt.setScore(80);

    when(studentQuizService.submitQuiz(userId, readingId, request)).thenReturn(attempt);

    ResponseEntity<QuizAttempt> response = controller.submitQuiz(readingId, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getScore()).isEqualTo(80);
  }

  // --- POST /api/student/readings/{readingId}/complete ---

  @Test
  void completeReading_ReturnsOk() {
    doNothing().when(studentQuizService).completeReading(userId, readingId);

    ResponseEntity<Void> response = controller.completeReading(readingId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    verify(studentQuizService).completeReading(userId, readingId);
  }

  // --- Unauthorized access ---

  @Test
  void getCurrentUserId_WhenNoAuth_ThrowsUnauthorized() {
    SecurityContextHolder.clearContext();

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> controller.getAvailableReadings());
    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void getCurrentUserId_WhenPrincipalNotSecurityUser_ThrowsUnauthorized() {
    UsernamePasswordAuthenticationToken badAuth =
        new UsernamePasswordAuthenticationToken("not-a-security-user", null);
    SecurityContextHolder.getContext().setAuthentication(badAuth);

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> controller.getAvailableReadings());
    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
