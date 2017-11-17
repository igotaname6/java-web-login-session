CREATE TABLE sessions(
  id TEXT PRIMARY KEY,
  user_id TEXT,
    FOREIGN KEY(user_id) REFERENCES users(id)
)