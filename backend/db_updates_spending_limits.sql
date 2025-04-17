-- Add spending limit columns to categories table
ALTER TABLE categories ADD COLUMN IF NOT EXISTS spending_limit NUMERIC(38, 2);
ALTER TABLE categories ADD COLUMN IF NOT EXISTS notification_threshold INTEGER DEFAULT 80;

-- Create category_limit_notification table
CREATE TABLE IF NOT EXISTS category_limit_notification (
    notification_id SERIAL PRIMARY KEY,
    category_id INTEGER REFERENCES categories(category_id),
    user_id INTEGER REFERENCES userprofile(user_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    message VARCHAR(255),
    is_read BOOLEAN DEFAULT FALSE
);

-- Create sequence for the new table
CREATE SEQUENCE IF NOT EXISTS category_limit_notification_seq START 1;
