package id.ac.ui.cs.advprog.yomu.backend.service;

import id.ac.ui.cs.advprog.yomu.backend.model.Reading;
import id.ac.ui.cs.advprog.yomu.backend.model.Question;

import java.util.UUID;

public interface AdminContentService {
    Reading createReading(Reading reading);

    void deleteReading(UUID readingId);

    Question addQuestionToReading(UUID readingId, Question question);

    // Tambahan untuk mengambil data (opsional untuk testing/validasi)
    Reading getReadingById(UUID readingId);
}
