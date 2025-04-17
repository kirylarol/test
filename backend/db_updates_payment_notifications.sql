-- Create payment_reminder table
CREATE TABLE payment_reminder (
    payment_reminder_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES userprofile(user_id),
    title VARCHAR(255) NOT NULL,
    amount NUMERIC(38, 2),
    due_date DATE NOT NULL,
    description TEXT,
    category_id INTEGER REFERENCES category(category_id),
    is_recurring BOOLEAN DEFAULT FALSE,
    recurrence_pattern VARCHAR(50),
    notification_days INTEGER DEFAULT 3,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create payment_notification table
CREATE TABLE payment_notification (
    payment_notification_id SERIAL PRIMARY KEY,
    payment_reminder_id INTEGER REFERENCES payment_reminder(payment_reminder_id),
    notification_date TIMESTAMP NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create sequences for the new tables
CREATE SEQUENCE payment_reminder_seq START 1;
CREATE SEQUENCE payment_notification_seq START 1;

-- Create indexes for better performance
CREATE INDEX idx_payment_reminder_user_id ON payment_reminder(user_id);
CREATE INDEX idx_payment_reminder_due_date ON payment_reminder(due_date);
CREATE INDEX idx_payment_reminder_status ON payment_reminder(status);
CREATE INDEX idx_payment_notification_reminder_id ON payment_notification(payment_reminder_id);
CREATE INDEX idx_payment_notification_date ON payment_notification(notification_date);
