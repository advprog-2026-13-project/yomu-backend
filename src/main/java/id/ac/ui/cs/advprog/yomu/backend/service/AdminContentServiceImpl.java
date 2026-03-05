package id.ac.ui.cs.advprog.yomu.backend.service;

import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import id.ac.ui.cs.advprog.yomu.backend.model.Question;
import id.ac.ui.cs.advprog.yomu.backend.repository.ReadingRepository;
import id.ac.ui.cs.advprog.yomu.backend.repository.QuestionRepository;
import id.ac.ui.cs.advprog.yomu.backend.service.AdminContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminContentServiceImpl implements AdminContentService {

    private final ReadingRepository readingRepository;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public Reading createReading(Reading reading) {
        // Validasi tambahan bisa diletakkan di sini sebelum save
        return readingRepository.save(reading);
    }

    @Override
    @Transactional
    public void deleteReading(UUID readingId) {
        // Karena kita set cascade = CascadeType.ALL di entity Reading,
        // menghapus reading otomatis menghapus semua question yang terikat.
        readingRepository.deleteById(readingId);
    }

    @Override
    @Transactional
    public Question addQuestionToReading(UUID readingId, Question question) {
        Reading reading = getReadingById(readingId);

        // Mengaitkan soal dengan bacaannya
        question.setReading(reading);

        return questionRepository.save(question);
    }

    @Override
    @Transactional(readOnly = true)
    public Reading getReadingById(UUID readingId) {
        return readingRepository.findById(readingId)
                .orElseThrow(() -> new RuntimeException("Reading dengan ID " + readingId + " tidak ditemukan."));
    }
}
