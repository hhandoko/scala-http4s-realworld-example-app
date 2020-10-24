-- Table: Profile (User + Author)
-- ~~~~~~
CREATE TABLE IF NOT EXISTS profile (
    id         BIGSERIAL NOT NULL,
    username   TEXT      NOT NULL,
    email      TEXT      NOT NULL,
    bio        TEXT,
    image      TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_profile_id       PRIMARY KEY (id),
    CONSTRAINT uq_profile_username UNIQUE      (username),
    CONSTRAINT uq_profile_email    UNIQUE      (email)
);

CREATE INDEX IF NOT EXISTS ix_profile_username
    ON profile
    USING btree (lower(username));

CREATE INDEX IF NOT EXISTS ix_profile_email
    ON profile
    USING btree (lower(email));
