CREATE TABLE quiz_attempt (
                              quiz_attempt_id UUID PRIMARY KEY,
                              student_id VARCHAR(255),
                              reading_id UUID,
                              score INTEGER,
                              completed_at TIMESTAMP(6)
);