CREATE TABLE IF NOT EXISTS reading (
    reading_id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(255),
    author_id VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS question (
    question_id UUID PRIMARY KEY,
    reading_id UUID NOT NULL,
    question_text TEXT,
    correct_answer VARCHAR(255),
    CONSTRAINT fk_question_reading FOREIGN KEY (reading_id) REFERENCES reading(reading_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS question_options (
    question_question_id UUID NOT NULL,
    options VARCHAR(255),
    CONSTRAINT fk_question_options_question FOREIGN KEY (question_question_id) REFERENCES question(question_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS quiz_attempt (
    quiz_attempt_id UUID PRIMARY KEY,
    student_id VARCHAR(255),
    reading_id UUID,
    score INTEGER,
    completed_at TIMESTAMP
);
