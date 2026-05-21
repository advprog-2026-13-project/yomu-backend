CREATE TABLE IF NOT EXISTS forum_comments (
    id UUID PRIMARY KEY,
    reading_id UUID NOT NULL,
    author_id UUID NOT NULL,
    author_name VARCHAR(100),
    parent_id UUID,
    content TEXT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edited_at TIMESTAMP,
    CONSTRAINT fk_forum_comments_parent FOREIGN KEY (parent_id) REFERENCES forum_comments(id)
);

CREATE INDEX IF NOT EXISTS idx_forum_comments_reading_id ON forum_comments(reading_id);
CREATE INDEX IF NOT EXISTS idx_forum_comments_parent_id ON forum_comments(parent_id);
CREATE INDEX IF NOT EXISTS idx_forum_comments_author_id ON forum_comments(author_id);

CREATE TABLE IF NOT EXISTS forum_comment_reactions (
    id UUID PRIMARY KEY,
    comment_id UUID NOT NULL,
    user_id UUID NOT NULL,
    type VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_forum_comment_reactions_comment FOREIGN KEY (comment_id) REFERENCES forum_comments(id) ON DELETE CASCADE,
    CONSTRAINT uk_forum_comment_reactions_unique UNIQUE (comment_id, user_id, type)
);

CREATE INDEX IF NOT EXISTS idx_forum_comment_reactions_comment_id ON forum_comment_reactions(comment_id);
CREATE INDEX IF NOT EXISTS idx_forum_comment_reactions_user_id ON forum_comment_reactions(user_id);
