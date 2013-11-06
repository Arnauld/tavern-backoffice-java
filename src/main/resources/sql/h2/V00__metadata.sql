CREATE TABLE metadata
(
  name  VARCHAR(255) PRIMARY KEY NOT NULL,
  value VARCHAR(255)
);

INSERT INTO metadata (name, value) VALUES ('db.version', '0.1.0');
