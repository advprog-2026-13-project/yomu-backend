CREATE TABLE reading (
                         reading_id UUID PRIMARY KEY,
                         title VARCHAR(255),
                         content TEXT,
                         category VARCHAR(255),
                         author_id VARCHAR(255)
);

CREATE TABLE question (
                          question_id UUID PRIMARY KEY,
                          question_text TEXT,
                          correct_answer VARCHAR(255),
                          reading_id UUID REFERENCES reading(reading_id)
);

CREATE TABLE question_options (
                                  question_question_id UUID NOT NULL REFERENCES question(question_id),
                                  options VARCHAR(255)
);