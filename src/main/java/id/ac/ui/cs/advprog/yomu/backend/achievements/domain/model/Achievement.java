package id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Achievement {
  private final UUID id;
  private final String name;
  private final String description;
  private final AchievementType achievementType;
  private final int milestone;

  public Achievement(
      UUID id, String name, String description, AchievementType achievementType, int milestone) {
    if (milestone <= 0) {
      throw new IllegalArgumentException("Milestone must be greater than 0");
    }
    this.id = id != null ? id : UUID.randomUUID();
    this.name = name;
    this.description = description;
    this.achievementType = achievementType;
    this.milestone = milestone;
  }

  /**
   * Creates a new Achievement with updated fields, preserving the original ID. Null values for
   * optional fields mean "keep the current value".
   *
   * @param newName the new name, or null to keep current
   * @param newDescription the new description, or null to keep current
   * @param newMilestone the new milestone, or null to keep current
   * @return a new Achievement instance with the updated values
   */
  public Achievement update(String newName, String newDescription, Integer newMilestone) {
    return new Achievement(
        this.id,
        newName != null ? newName : this.name,
        newDescription != null ? newDescription : this.description,
        this.achievementType,
        newMilestone != null ? newMilestone : this.milestone);
  }
}
