package id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import java.util.UUID;
import lombok.Getter;

@Getter
public class DailyMission {
  private final UUID id;
  private final String name;
  private final String description;
  private final AchievementType targetType;
  private final int milestone;

  public DailyMission(
      UUID id, String name, String description, AchievementType targetType, int milestone) {
    if (milestone <= 0) {
      throw new IllegalArgumentException("Milestone must be greater than 0");
    }
    this.id = id != null ? id : UUID.randomUUID();
    this.name = name;
    this.description = description;
    this.targetType = targetType;
    this.milestone = milestone;
  }

  /**
   * Creates a new DailyMission with updated fields, preserving the original ID. Null values for
   * optional fields mean "keep the current value".
   *
   * @param newName the new name, or null to keep current
   * @param newDescription the new description, or null to keep current
   * @param newMilestone the new milestone, or null to keep current
   * @return a new DailyMission instance with the updated values
   */
  public DailyMission update(String newName, String newDescription, Integer newMilestone) {
    return new DailyMission(
        this.id,
        newName != null ? newName : this.name,
        newDescription != null ? newDescription : this.description,
        this.targetType,
        newMilestone != null ? newMilestone : this.milestone);
  }
}
