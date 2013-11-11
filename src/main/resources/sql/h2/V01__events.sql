CREATE TABLE events
(
  entity_id   VARCHAR(36)     NOT NULL,
  version     INT             NOT NULL,
  payload     VARCHAR(20000)  NOT NULL,
  event_type  VARCHAR(255)    NOT NULL,
  creation_ts TIMESTAMP       NOT NULL,
  PRIMARY KEY (entity_id, version)
);

UPDATE `metadata`
SET `value`= '0.1.1'
WHERE `name` = 'db.version';