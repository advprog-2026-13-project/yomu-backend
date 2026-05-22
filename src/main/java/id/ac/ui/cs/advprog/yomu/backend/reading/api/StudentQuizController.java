package id.ac.ui.cs.advprog.yomu.backend.reading.api;

import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import id.ac.ui.cs.advprog.yomu.backend.reading.api.dto.QuestionResponse;
import id.ac.ui.cs.advprog.yomu.backend.reading.api.dto.QuizSubmissionRequest;
import id.ac.ui.cs.advprog.yomu.backend.reading.application.StudentQuizService;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.Reading;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentQuizController {

  private final StudentQuizService studentQuizService;

  @GetMapping("/readings")
  public ResponseEntity<List<Reading>> getAvailableReadings() {
    var userId = getCurrentUserId();
    return ResponseEntity.ok(studentQuizService.getAllReadingsWithCompletionStatus(userId));
  }

  @GetMapping("/readings/{readingId}")
  public ResponseEntity<Reading> getReading(@PathVariable UUID readingId) {
    var userId = getCurrentUserId();
    return ResponseEntity.ok(studentQuizService.getReadingForStudent(userId, readingId));
  }

  @GetMapping("/readings/{readingId}/questions")
  public ResponseEntity<List<QuestionResponse>> getQuestions(@PathVariable UUID readingId) {
    var userId = getCurrentUserId();
    return ResponseEntity.ok(studentQuizService.getQuestionsForReading(userId, readingId));
  }

  @PostMapping("/readings/{readingId}/submit")
  public ResponseEntity<QuizAttempt> submitQuiz(
      @PathVariable UUID readingId, @RequestBody QuizSubmissionRequest request) {
    var userId = getCurrentUserId();
    return ResponseEntity.ok(studentQuizService.submitQuiz(userId, readingId, request));
  }

  @PostMapping("/readings/{readingId}/complete")
  public ResponseEntity<Void> completeReading(@PathVariable UUID readingId) {
    var userId = getCurrentUserId();
    studentQuizService.completeReading(userId, readingId);
    return ResponseEntity.ok().build();
  }

  private UUID getCurrentUserId() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof SecurityUser user)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    return user.getUser().getId();
  }
}
