package id.ac.ui.cs.advprog.yomu.backend.reading.application;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementEnvelope;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementQuizCompletedPayload;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload.AchievementReadingCompletedPayload;
import id.ac.ui.cs.advprog.yomu.backend.reading.api.dto.QuestionResponse;
import id.ac.ui.cs.advprog.yomu.backend.reading.api.dto.QuizSubmissionRequest;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.Question;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.backend.reading.domain.Reading;
import id.ac.ui.cs.advprog.yomu.backend.reading.infrastructure.QuestionRepository;
import id.ac.ui.cs.advprog.yomu.backend.reading.infrastructure.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomu.backend.reading.infrastructure.ReadingRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional(readOnly = true)
  public List<Reading> getAllReadingsWithCompletionStatus(UUID userId) {
    var studentId = userId.toString();
    List<Reading> visibleReadings = readingRepository.findByHiddenFalse();
    for (Reading reading : visibleReadings) {
      boolean isCompleted =
          quizAttemptRepository.existsByStudentIdAndReadingId(studentId, reading.getReadingId());
      reading.setCompleted(isCompleted);
    }
    return visibleReadings;
  }

  @Override
  @Transactional(readOnly = true)
  public Reading getReadingForStudent(UUID userId, UUID readingId) {
    Reading reading =
        readingRepository
            .findById(readingId)
            .orElseThrow(() -> new RuntimeException("Bacaan tidak ditemukan"));
    boolean isCompleted =
        quizAttemptRepository.existsByStudentIdAndReadingId(userId.toString(), readingId);
    reading.setCompleted(isCompleted);
    return reading;
  }

  @Override
  @Transactional(readOnly = true)
  public List<QuestionResponse> getQuestionsForReading(UUID userId, UUID readingId) {
    List<Question> questions = questionRepository.findByReading_ReadingId(readingId);
    List<QuestionResponse> responses = new ArrayList<>();
    for (Question q : questions) {
      QuestionResponse dto = new QuestionResponse();
      dto.setQuestionId(q.getQuestionId());
      dto.setQuestionText(q.getQuestionText());
      dto.setOptions(new ArrayList<>(q.getOptions()));
      responses.add(dto);
    }
    return responses;
  }

  @Override
  @Transactional
  public QuizAttempt submitQuiz(UUID userId, UUID readingId, QuizSubmissionRequest request) {
    validateNotAttempted(userId, readingId);
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
    attempt.setStudentId(userId);
    attempt.setReadingId(readingId);
    attempt.setScore(score);
    attempt.setCompletedAt(LocalDateTime.now());
    QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);

    // Publish Achievement Events
    publishAchievementEvents(userId, readingId, score);

    return savedAttempt;
  }

  @Override
  @Transactional
  public void completeReading(UUID userId, UUID readingId) {
    validateNotAttempted(userId, readingId);

    QuizAttempt attempt = new QuizAttempt();
    attempt.setStudentId(userId.toString());
    attempt.setReadingId(readingId);
    attempt.setScore(100); // 100 as default score for completing reading material
    attempt.setCompletedAt(java.time.LocalDateTime.now());
    quizAttemptRepository.save(attempt);

    // Publish Reading Completed Event
    AchievementReadingCompletedPayload readingPayload =
        new AchievementReadingCompletedPayload(userId, readingId, 0); // Duration not tracked yet
    eventPublisher.publishEvent(
        AchievementEnvelope.of(AchievementType.READING_COMPLETED, 1, readingPayload));
  }

  private void publishAchievementEvents(UUID userId, UUID readingId, int score) {
    // 1. Publish Reading Completed Event
    AchievementReadingCompletedPayload readingPayload =
        new AchievementReadingCompletedPayload(userId, readingId, 0); // Duration not tracked yet
    eventPublisher.publishEvent(
        AchievementEnvelope.of(AchievementType.READING_COMPLETED, 1, readingPayload));

    // 2. Publish Quiz Completed Event
    AchievementQuizCompletedPayload quizPayload =
        new AchievementQuizCompletedPayload(userId, readingId, score, true);
    eventPublisher.publishEvent(
        AchievementEnvelope.of(AchievementType.QUIZ_COMPLETED, 1, quizPayload));
  }

  private void validateNotAttempted(UUID userId, UUID readingId) {
    if (quizAttemptRepository.existsByStudentIdAndReadingId(userId, readingId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "...");
    }
  }
}
