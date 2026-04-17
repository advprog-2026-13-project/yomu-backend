package id.ac.ui.cs.advprog.yomu.backend.controller;

import id.ac.ui.cs.advprog.yomu.backend.model.Question;
import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import id.ac.ui.cs.advprog.yomu.backend.service.AdminContentService;
import jakarta.validation.Valid; // Ensure validation is used
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminContentController {

  private final AdminContentService adminContentService;

  @GetMapping("/readings")
  public ResponseEntity<List<Reading>> getAllReadings() {
    return ResponseEntity.ok(adminContentService.getAllReadings());
  }

  @GetMapping("/readings/{id}")
  public ResponseEntity<Reading> getReadingById(@PathVariable UUID id) {
    return ResponseEntity.ok(adminContentService.getReadingById(id));
  }

  @PostMapping("/readings")
  public ResponseEntity<Reading> createReading(@Valid @RequestBody Reading reading) {
    return ResponseEntity.ok(adminContentService.createReading(reading));
  }

  @PutMapping("/readings/{id}")
  public ResponseEntity<Reading> updateReading(
      @PathVariable UUID id, @Valid @RequestBody Reading updatedReading) {
    return ResponseEntity.ok(adminContentService.updateReading(id, updatedReading));
  }

  @DeleteMapping("/readings/{id}")
  public ResponseEntity<Void> deleteReading(@PathVariable UUID id) {
    adminContentService.deleteReading(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/readings/{readingId}/questions")
  public ResponseEntity<Question> addQuestion(
      @PathVariable UUID readingId, @Valid @RequestBody Question question) {
    return ResponseEntity.ok(adminContentService.addQuestionToReading(readingId, question));
  }

  @PutMapping("/questions/{questionId}")
  public ResponseEntity<Question> updateQuestion(
      @PathVariable UUID questionId, @Valid @RequestBody Question updatedQuestion) {
    return ResponseEntity.ok(adminContentService.updateQuestion(questionId, updatedQuestion));
  }

  @DeleteMapping("/questions/{questionId}")
  public ResponseEntity<Void> deleteQuestion(@PathVariable UUID questionId) {
    adminContentService.deleteQuestion(questionId);
    return ResponseEntity.noContent().build();
  }
}
