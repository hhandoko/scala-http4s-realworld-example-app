-- Table: Article
-- ~~~~~~
CREATE TABLE IF NOT EXISTS article (
    id          BIGSERIAL NOT NULL,
    slug        TEXT      NOT NULL,
    title       TEXT      NOT NULL,
    description TEXT      NOT NULL,
    body        TEXT      NOT NULL,
    author_id   BIGINT    NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_article_id      PRIMARY KEY (id),
    CONSTRAINT uq_article_slug    UNIQUE      (author_id, slug),
    CONSTRAINT fk_article_profile FOREIGN KEY (author_id)
        REFERENCES profile (id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS ix_article_author
    ON article (author_id);
