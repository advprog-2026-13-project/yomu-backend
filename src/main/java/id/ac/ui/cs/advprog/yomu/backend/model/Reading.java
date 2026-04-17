package id.ac.ui.cs.advprog.yomu.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
public class Reading {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID readingId;

  @NotBlank(message = "Title cannot be empty")
  private String title;

  @NotNull(message = "Content is required")
  @Column(columnDefinition = "TEXT")
  private String content;

  private String category;

  private String authorId;

  @JsonIgnore
  @OneToMany(mappedBy = "reading", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Question> questions;
}
