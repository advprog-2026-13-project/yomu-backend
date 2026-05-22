CREATE TABLE achievements (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    achievement_type VARCHAR(50) NOT NULL,
    milestone INT NOT NULL
);

CREATE TABLE user_achievement_progress (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    achievement_id UUID NOT NULL,
    current_progress INT NOT NULL DEFAULT 0,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMP,
    is_displayed_on_profile BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_user_achievement_achievement FOREIGN KEY (achievement_id) REFERENCES achievements(id),
    CONSTRAINT fk_user_achievement_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (user_id, achievement_id)
);

CREATE TABLE daily_missions (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    target_type VARCHAR(50) NOT NULL,
    milestone INT NOT NULL
);

CREATE TABLE user_daily_mission_progress (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    mission_id UUID NOT NULL,
    date DATE NOT NULL,
    current_progress INT NOT NULL DEFAULT 0,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMP,
    CONSTRAINT fk_user_daily_mission_mission FOREIGN KEY (mission_id) REFERENCES daily_missions(id),
    CONSTRAINT fk_user_daily_mission_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (user_id, mission_id, date)
);
