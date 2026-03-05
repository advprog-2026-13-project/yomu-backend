package id.ac.ui.cs.advprog.yomu.backend.service;

import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import id.ac.ui.cs.advprog.yomu.backend.model.Question;
import id.ac.ui.cs.advprog.yomu.backend.repository.ReadingRepository;
import id.ac.ui.cs.advprog.yomu.backend.repository.QuestionRepository;
import id.ac.ui.cs.advprog.yomu.backend.service.AdminContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminContentServiceImpl implements AdminContentService {

    private final ReadingRepository readingRepository;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public Reading createReading(Reading reading) {
        return readingRepository.save(reading);
    }

    @Override
    @Transactional(readOnly = true)
    public Reading getReadingById(UUID readingId) {
        return readingRepository.findById(readingId)
                .orElseThrow(() -> new RuntimeException("Reading dengan ID " + readingId + " tidak ditemukan."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reading> getAllReadings() {
        return readingRepository.findAll();
    }

    @Override
    @Transactional
    public Reading updateReading(UUID id, Reading updatedReading) {
        Reading existingReading = getReadingById(id);

        existingReading.setTitle(updatedReading.getTitle());
        existingReading.setContent(updatedReading.getContent());
        existingReading.setCategory(updatedReading.getCategory());

        return readingRepository.save(existingReading);
    }

    @Override
    @Transactional
    public void deleteReading(UUID readingId) {
        readingRepository.deleteById(readingId);
    }

    @Override
    @Transactional
    public Question addQuestionToReading(UUID readingId, Question question) {
        Reading reading = getReadingById(readingId);
        question.setReading(reading);
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public Question updateQuestion(UUID questionId, Question updatedQuestion) {
        Question existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question dengan ID " + questionId + " tidak ditemukan."));

        existingQuestion.setQuestionText(updatedQuestion.getQuestionText());
        existingQuestion.setOptions(updatedQuestion.getOptions());
        existingQuestion.setCorrectAnswer(updatedQuestion.getCorrectAnswer());

        return questionRepository.save(existingQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestion(UUID questionId) {
        questionRepository.deleteById(questionId);
    }
}
