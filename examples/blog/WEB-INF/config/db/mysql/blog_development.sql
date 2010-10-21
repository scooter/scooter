DROP DATABASE IF EXISTS blog_development;

CREATE DATABASE blog_development DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

USE blog_development;

DROP TABLE IF EXISTS posts;

CREATE TABLE posts (
  id int(11) NOT NULL auto_increment,
  name       varchar(255),
  title      varchar(255),
  content    text,
  created_at timestamp,
  updated_at timestamp,
  PRIMARY KEY  (id)
) ENGINE=InnoDB;

CREATE TABLE comments (
  id int(11) NOT NULL auto_increment,
  commenter  varchar(255),
  body       text,
  post_id    int(11),
  created_at timestamp,
  updated_at timestamp,
  PRIMARY KEY  (id)
) ENGINE=InnoDB;

ALTER TABLE posts ADD COLUMN comments_count int DEFAULT 0;