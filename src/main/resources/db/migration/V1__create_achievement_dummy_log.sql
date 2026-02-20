CREATE TABLE IF NOT EXISTS achievement_dummy_log (
    id BIGSERIAL PRIMARY KEY,
    event_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );