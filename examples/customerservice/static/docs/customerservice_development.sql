DROP DATABASE IF EXISTS customerservice_development;

CREATE DATABASE customerservice_development DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

USE customerservice_development;

DROP TABLE IF EXISTS entries;

CREATE TABLE entries (
  id int(11) NOT NULL auto_increment,
  name       varchar(30),
  content    text,
  created_at timestamp,
  PRIMARY KEY  (id)
) ENGINE=InnoDB;