package id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model;

import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Achievement {
    private final UUID id;
    private final String name;
    private final String description;
    private final AchievementType achievementType;
    private final int milestone;

    public Achievement(UUID id, String name, String description, AchievementType achievementType, int milestone) {
        if (milestone <= 0) {
            throw new IllegalArgumentException("Milestone must be greater than 0");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.achievementType = achievementType;
        this.milestone = milestone;
    }
}
