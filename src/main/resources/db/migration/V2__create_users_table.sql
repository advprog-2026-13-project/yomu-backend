CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY,
                                     username VARCHAR(40) NOT NULL,
    email VARCHAR(120) NOT NULL,
    password_hash VARCHAR(200) NOT NULL,
    role VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
    );