-- Table: Authentication
-- ~~~~~~
CREATE TABLE IF NOT EXISTS auth(
    id          BIGSERIAL NOT NULL,
    profile_id  BIGINT    NOT NULL,
    password    VARCHAR,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_auth_id      PRIMARY KEY (id),
    CONSTRAINT fk_auth_profile FOREIGN KEY (profile_id) REFERENCES profile (id)
);
