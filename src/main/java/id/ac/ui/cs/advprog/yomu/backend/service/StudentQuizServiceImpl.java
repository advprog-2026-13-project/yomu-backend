package id.ac.ui.cs.advprog.yomu.backend.service;

import id.ac.ui.cs.advprog.yomu.backend.dto.QuestionResponse;
import id.ac.ui.cs.advprog.yomu.backend.dto.QuizSubmissionRequest;
import id.ac.ui.cs.advprog.yomu.backend.model.Question;
import id.ac.ui.cs.advprog.yomu.backend.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import id.ac.ui.cs.advprog.yomu.backend.repository.QuestionRepository;
import id.ac.ui.cs.advprog.yomu.backend.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomu.backend.repository.ReadingRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StudentQuizServiceImpl implements StudentQuizService {

  private final ReadingRepository readingRepository;
  private final QuestionRepository questionRepository;
  private final QuizAttemptRepository quizAttemptRepository;

  @Override
  @Transactional(readOnly = true)
  public List<Reading> getAvailableReadingsForStudent(String studentId) {
    return readingRepository.findAll().stream()
        .filter(
            reading ->
                !quizAttemptRepository.existsByStudentIdAndReadingId(
                    studentId, reading.getReadingId()))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Reading getReadingForStudent(String studentId, UUID readingId) {
    validateNotAttempted(studentId, readingId);
    return readingRepository
        .findById(readingId)
        .orElseThrow(() -> new RuntimeException("Bacaan tidak ditemukan"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<QuestionResponse> getQuestionsForReading(String studentId, UUID readingId) {
    validateNotAttempted(studentId, readingId);

    List<Question> questions = questionRepository.findByReading_ReadingId(readingId);
    List<QuestionResponse> responses = new ArrayList<>();

    for (Question q : questions) {
      QuestionResponse dto = new QuestionResponse();
      dto.setQuestionId(q.getQuestionId());
      dto.setQuestionText(q.getQuestionText());
      dto.setOptions(q.getOptions());
      responses.add(dto);
    }
    return responses;
  }

  @Override
  @Transactional
  public QuizAttempt submitQuiz(UUID readingId, QuizSubmissionRequest request) {
    validateNotAttempted(request.getStudentId(), readingId);

    List<Question> correctQuestions = questionRepository.findByReading_ReadingId(readingId);

    if (correctQuestions.isEmpty()) {
      throw new RuntimeException("Kuis ini belum memiliki soal.");
    }

    int correctCount = 0;

    for (QuizSubmissionRequest.StudentAnswer studentAnswer : request.getAnswers()) {
      for (Question question : correctQuestions) {
        if (question.getQuestionId().equals(studentAnswer.getQuestionId())) {
          if (question.getCorrectAnswer().equalsIgnoreCase(studentAnswer.getSelectedAnswer())) {
            correctCount++;
          }
          break;
        }
      }
    }

    int score = (int) Math.round(((double) correctCount / correctQuestions.size()) * 100);

    QuizAttempt attempt = new QuizAttempt();
    attempt.setStudentId(request.getStudentId());
    attempt.setReadingId(readingId);
    attempt.setScore(score);
    attempt.setCompletedAt(LocalDateTime.now());

    QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);

    // TODO: Di sini nembak event/notifikasi ke Modul Achievements & Liga

    return savedAttempt;
  }

  private void validateNotAttempted(String studentId, UUID readingId) {
    if (quizAttemptRepository.existsByStudentIdAndReadingId(studentId, readingId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "...");
    }
  }
}
