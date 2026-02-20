package id.ac.ui.cs.advprog.yomu.backend.achievements;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "achievement_dummy_log")
public class AchievementDummyLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "event_name", nullable = false)
  private String eventName;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  void prePersist() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }

  public Long getId() {
    return id;
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
