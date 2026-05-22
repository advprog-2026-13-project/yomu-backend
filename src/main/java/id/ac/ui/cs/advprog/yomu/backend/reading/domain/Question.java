package id.ac.ui.cs.advprog.yomu.backend.reading.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
public class Question {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID questionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reading_id")
  @JsonIgnore
  private Reading reading;

  @Column(columnDefinition = "TEXT")
  private String questionText;

  @ElementCollection(fetch = FetchType.EAGER)
  private List<String> options;

  private String correctAnswer;
}
