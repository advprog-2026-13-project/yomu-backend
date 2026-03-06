package id.ac.ui.cs.advprog.yomu.backend.controller;

import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import id.ac.ui.cs.advprog.yomu.backend.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.backend.dto.QuestionResponse;
import id.ac.ui.cs.advprog.yomu.backend.dto.QuizSubmissionRequest;
import id.ac.ui.cs.advprog.yomu.backend.service.StudentQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentQuizController {

    private final StudentQuizService studentQuizService;

    @GetMapping("/readings")
    public ResponseEntity<List<Reading>> getAvailableReadings(@RequestParam String studentId) {
        return ResponseEntity.ok(studentQuizService.getAvailableReadingsForStudent(studentId));
    }

    @GetMapping("/readings/{readingId}")
    public ResponseEntity<Reading> getReading(@PathVariable UUID readingId, @RequestParam String studentId) {
        return ResponseEntity.ok(studentQuizService.getReadingForStudent(studentId, readingId));
    }

    @GetMapping("/readings/{readingId}/questions")
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @PathVariable UUID readingId,
            @RequestParam String studentId) {
        return ResponseEntity.ok(studentQuizService.getQuestionsForReading(studentId, readingId));
    }

    @PostMapping("/readings/{readingId}/submit")
    public ResponseEntity<QuizAttempt> submitQuiz(
            @PathVariable UUID readingId,
            @RequestBody QuizSubmissionRequest request) {
        return ResponseEntity.ok(studentQuizService.submitQuiz(readingId, request));
    }
}
