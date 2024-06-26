ALTER TABLE users
ADD COLUMN IF NOT EXISTS username VARCHAR(255) NOT NULL,
ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255) NOT NULL,
ADD COLUMN IF NOT EXISTS account_locked BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS failed_login_attempts INT NOT NULL DEFAULT 0;