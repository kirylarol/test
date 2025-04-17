-- Create income_category table
CREATE TABLE IF NOT EXISTS income_category (
    income_category_id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    description TEXT
);

-- Create income table
CREATE TABLE IF NOT EXISTS income (
    income_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES userprofile(user_id),
    account_id INTEGER REFERENCES account(account_id),
    income_category_id INTEGER REFERENCES income_category(income_category_id),
    amount NUMERIC(38, 2),
    description VARCHAR(255),
    date DATE,
    is_recurring BOOLEAN DEFAULT FALSE,
    recurrence_period VARCHAR(50)
);

-- Create sequences for the new tables
CREATE SEQUENCE IF NOT EXISTS income_category_seq START 1;
CREATE SEQUENCE IF NOT EXISTS income_seq START 1;
