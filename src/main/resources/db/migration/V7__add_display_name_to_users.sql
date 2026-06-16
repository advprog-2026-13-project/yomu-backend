ALTER TABLE users ADD COLUMN display_name VARCHAR(100) NOT NULL;
ALTER TABLE users ADD COLUMN phone_number VARCHAR(20);
ALTER TABLE users ADD COLUMN google_sub VARCHAR(255);

ALTER TABLE users ADD CONSTRAINT uk_users_phone UNIQUE (phone_number);
ALTER TABLE users ADD CONSTRAINT uk_users_google_sub UNIQUE (google_sub);