package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("unused")
@Entity
@Table(
    name = "forum_comment_reactions",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_forum_comment_reactions_unique",
          columnNames = {"comment_id", "user_id", "type"})
    },
    indexes = {
      @Index(name = "idx_forum_comment_reactions_comment_id", columnList = "comment_id"),
      @Index(name = "idx_forum_comment_reactions_user_id", columnList = "user_id")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReactionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "comment_id", nullable = false)
  private UUID commentId;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 30)
  private ReactionType type;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  @SuppressWarnings("unused")
  void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
