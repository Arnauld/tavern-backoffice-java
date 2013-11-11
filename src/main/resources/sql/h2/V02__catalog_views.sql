CREATE TABLE catalog_views
(
  catalog_id  VARCHAR(36) PRIMARY KEY NOT NULL,
  version     INT                     NOT NULL,
  label       VARCHAR(36)             NOT NULL
);

CREATE TABLE catalog_entry_views
(
  catalog_id  VARCHAR(36)  NOT NULL,
  entry_id    VARCHAR(36)  NOT NULL,
  version     INT          NOT NULL,
  label       VARCHAR(36)  NOT NULL,
  price       VARCHAR(36)  NOT NULL,
  PRIMARY KEY (catalog_id, entry_id)
);

UPDATE `metadata`
SET `value`= '0.1.2'
WHERE `name` = 'db.version';
