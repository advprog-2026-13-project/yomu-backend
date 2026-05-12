package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "forum_comments",
    indexes = {
      @Index(name = "idx_forum_comments_reading_id", columnList = "reading_id"),
      @Index(name = "idx_forum_comments_parent_id", columnList = "parent_id"),
      @Index(name = "idx_forum_comments_author_id", columnList = "author_id")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "reading_id", nullable = false)
  private UUID readingId;

  @Column(name = "author_id", nullable = false)
  private UUID authorId;

  @Column(name = "parent_id")
  private UUID parentId;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private boolean deleted = false;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "edited_at")
  private Instant editedAt;

  @PrePersist
  @SuppressWarnings("unused")
  void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
