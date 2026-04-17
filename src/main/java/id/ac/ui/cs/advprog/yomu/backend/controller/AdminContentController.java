package id.ac.ui.cs.advprog.yomu.backend.controller;

import id.ac.ui.cs.advprog.yomu.backend.dto.QuestionDTO;
import id.ac.ui.cs.advprog.yomu.backend.dto.ReadingDTO;
import id.ac.ui.cs.advprog.yomu.backend.model.Question;
import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import id.ac.ui.cs.advprog.yomu.backend.service.AdminContentService;
import jakarta.validation.Valid;
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
  public ResponseEntity<Reading> createReading(@Valid @RequestBody ReadingDTO readingDto) {
    // Service should now take DTO and convert to Entity internally
    return ResponseEntity.ok(adminContentService.createReading(readingDto));
  }

  @PutMapping("/readings/{id}")
  public ResponseEntity<Reading> updateReading(
          @PathVariable UUID id, @Valid @RequestBody ReadingDTO updatedReadingDto) {
    return ResponseEntity.ok(adminContentService.updateReading(id, updatedReadingDto));
  }

  @DeleteMapping("/readings/{id}")
  public ResponseEntity<Void> deleteReading(@PathVariable UUID id) {
    adminContentService.deleteReading(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/readings/{readingId}/questions")
  public ResponseEntity<Question> addQuestion(
          @PathVariable UUID readingId, @Valid @RequestBody QuestionDTO questionDto) {
    return ResponseEntity.ok(adminContentService.addQuestionToReading(readingId, questionDto));
  }

  @PutMapping("/questions/{questionId}")
  public ResponseEntity<Question> updateQuestion(
          @PathVariable UUID questionId, @Valid @RequestBody QuestionDTO updatedQuestionDto) {
    return ResponseEntity.ok(adminContentService.updateQuestion(questionId, updatedQuestionDto));
  }

  @DeleteMapping("/questions/{questionId}")
  public ResponseEntity<Void> deleteQuestion(@PathVariable UUID questionId) {
    adminContentService.deleteQuestion(questionId);
    return ResponseEntity.noContent().build();
  }
}