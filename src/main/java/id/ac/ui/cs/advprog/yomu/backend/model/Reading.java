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
public class Reading {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID readingId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String category;

    private String authorId;

    @JsonIgnore
    @OneToMany(mappedBy = "reading", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;
}
