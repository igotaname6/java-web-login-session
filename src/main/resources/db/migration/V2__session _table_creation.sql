CREATE TABLE sessions(
  id TEXT PRIMARY KEY,
  user_ud TEXT,
    FOREIGN KEY(user_ud) REFERENCES users(id)
)