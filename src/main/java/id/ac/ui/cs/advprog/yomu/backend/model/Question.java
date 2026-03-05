package id.ac.ui.cs.advprog.yomu.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter @Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_id")
    // Mencegah infinite recursion saat serialisasi JSON
    @JsonIgnore
    private Reading reading;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    @ElementCollection
    private List<String> options;

    private String correctAnswer;
}
