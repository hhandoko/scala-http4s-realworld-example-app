-- Table: Profile (User + Author)
-- ~~~~~~
CREATE TABLE IF NOT EXISTS profile(
    id         BIGSERIAL NOT NULL,
    username   VARCHAR   NOT NULL,
    email      VARCHAR   NOT NULL,
    bio        VARCHAR,
    image      VARCHAR,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_profile_id       PRIMARY KEY (id),
    CONSTRAINT uq_profile_username UNIQUE      (username),
    CONSTRAINT uq_profile_email    UNIQUE      (email)
);

CREATE INDEX IF NOT EXISTS ix_profile_username
    ON profile (username);

CREATE INDEX IF NOT EXISTS ix_profile_email
    ON profile (email);
