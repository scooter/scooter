DROP DATABASE IF EXISTS ajaxtodo_development;

CREATE DATABASE ajaxtodo_development DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

USE ajaxtodo_development;

DROP TABLE IF EXISTS entries;

CREATE TABLE entries (
  id INT(11) NOT NULL AUTO_INCREMENT,
  subject    VARCHAR(100),
  details    VARCHAR(500),
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  PRIMARY KEY  (id)
) ENGINE=InnoDB;