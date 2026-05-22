package id.ac.ui.cs.advprog.yomu.backend.reading.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.reading.api.dto.QuestionDTO;
import id.ac.ui.cs.advprog.yomu.backend.reading.api.dto.ReadingDTO;
import id.ac.ui.cs.advprog.yomu.backend.reading.application.AdminContentService;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.Question;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.Reading;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AdminContentControllerTest {

  @Mock private AdminContentService adminContentService;

  private AdminContentController controller;

  private UUID readingId;
  private UUID questionId;
  private Reading reading;

  @BeforeEach
  void setUp() {
    controller = new AdminContentController(adminContentService);
    readingId = UUID.randomUUID();
    questionId = UUID.randomUUID();
    reading = new Reading();
    reading.setReadingId(readingId);
    reading.setTitle("Test Reading");
    reading.setContent("Test Content");
  }

  // --- GET /api/admin/readings ---

  @Test
  void getAllReadings_ReturnsOkWithReadings() {
    when(adminContentService.getAllReadings()).thenReturn(List.of(reading));

    ResponseEntity<List<Reading>> response = controller.getAllReadings();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(1);
    assertThat(response.getBody().get(0).getTitle()).isEqualTo("Test Reading");
  }

  // --- GET /api/admin/readings/{id} ---

  @Test
  void getReadingById_ReturnsOkWithReading() {
    when(adminContentService.getReadingById(readingId)).thenReturn(reading);

    ResponseEntity<Reading> response = controller.getReadingById(readingId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReadingId()).isEqualTo(readingId);
  }

  // --- POST /api/admin/readings ---

  @Test
  void createReading_ReturnsOkWithCreatedReading() {
    ReadingDTO dto = new ReadingDTO("New Title", "New Content", "Author");
    when(adminContentService.createReading(any(ReadingDTO.class))).thenReturn(reading);

    ResponseEntity<Reading> response = controller.createReading(dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    verify(adminContentService).createReading(dto);
  }

  // --- PUT /api/admin/readings/{id} ---

  @Test
  void updateReading_ReturnsOkWithUpdatedReading() {
    ReadingDTO dto = new ReadingDTO("Updated Title", "Updated Content", "Author");
    Reading updatedReading = new Reading();
    updatedReading.setReadingId(readingId);
    updatedReading.setTitle("Updated Title");
    when(adminContentService.updateReading(eq(readingId), any(ReadingDTO.class)))
        .thenReturn(updatedReading);

    ResponseEntity<Reading> response = controller.updateReading(readingId, dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getTitle()).isEqualTo("Updated Title");
  }

  // --- DELETE /api/admin/readings/{id} ---

  @Test
  void deleteReading_ReturnsNoContent() {
    doNothing().when(adminContentService).deleteReading(readingId);

    ResponseEntity<Void> response = controller.deleteReading(readingId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(adminContentService).deleteReading(readingId);
  }

  // --- PATCH /api/admin/readings/{id}/hide ---

  @Test
  void hideReading_ReturnsNoContent() {
    doNothing().when(adminContentService).hideReading(readingId);

    ResponseEntity<Void> response = controller.hideReading(readingId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(adminContentService).hideReading(readingId);
  }

  // --- PATCH /api/admin/readings/{id}/unhide ---

  @Test
  void unhideReading_ReturnsNoContent() {
    doNothing().when(adminContentService).unhideReading(readingId);

    ResponseEntity<Void> response = controller.unhideReading(readingId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(adminContentService).unhideReading(readingId);
  }

  // --- POST /api/admin/readings/{readingId}/questions ---

  @Test
  void addQuestion_ReturnsOkWithCreatedQuestion() {
    QuestionDTO dto = new QuestionDTO("What is Java?", List.of("A", "B"), "A");
    Question question = new Question();
    question.setQuestionId(questionId);
    question.setQuestionText("What is Java?");

    when(adminContentService.addQuestionToReading(eq(readingId), any(QuestionDTO.class)))
        .thenReturn(question);

    ResponseEntity<Question> response = controller.addQuestion(readingId, dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getQuestionText()).isEqualTo("What is Java?");
  }

  // --- PUT /api/admin/questions/{questionId} ---

  @Test
  void updateQuestion_ReturnsOkWithUpdatedQuestion() {
    QuestionDTO dto = new QuestionDTO("Updated?", List.of("X", "Y"), "X");
    Question updatedQuestion = new Question();
    updatedQuestion.setQuestionId(questionId);
    updatedQuestion.setQuestionText("Updated?");

    when(adminContentService.updateQuestion(eq(questionId), any(QuestionDTO.class)))
        .thenReturn(updatedQuestion);

    ResponseEntity<Question> response = controller.updateQuestion(questionId, dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getQuestionText()).isEqualTo("Updated?");
  }

  // --- DELETE /api/admin/questions/{questionId} ---

  @Test
  void deleteQuestion_ReturnsNoContent() {
    doNothing().when(adminContentService).deleteQuestion(questionId);

    ResponseEntity<Void> response = controller.deleteQuestion(questionId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(adminContentService).deleteQuestion(questionId);
  }
}
