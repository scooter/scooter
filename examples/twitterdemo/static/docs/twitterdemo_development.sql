DROP DATABASE IF EXISTS twitterdemo_development;

CREATE DATABASE twitterdemo_development;

USE twitterdemo_development;

--
-- accounts and followships tables to test self-referential
--
-- accounts:
--
-- 1 Scooter/demo
-- 2 Java/demo
-- 3 Rails/demo
-- 4 John/demo
--
--
-- accounts(tweets) and followings:
--
-- 1.Scooter(2) ---> 2.Java
--                 | 3.Rails
-- 2.Java   (1)
-- 3.Rails  (2)
-- 4.John   (2) ---> 1.Scooter
--                 | 2.Java
--

CREATE TABLE accounts (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(10) NOT NULL,
  password VARCHAR(10) NOT NULL,
  tweets_count INT(4) NOT NULL DEFAULT 0,
  followers_count INT(4) NOT NULL DEFAULT 0,
  followings_count INT(4) NOT NULL DEFAULT 0
) engine=InnoDB;

CREATE TABLE followships (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  follower_id INT(4),
  following_id INT(4),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) engine=InnoDB;

CREATE TABLE tweets (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_id INT(4),
  message VARCHAR(140),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) engine=InnoDB;

INSERT INTO accounts VALUES(1,'Scooter', 'demo', 2, 1, 2);
INSERT INTO accounts VALUES(2,'Java', 'demo',    1, 2, 0);
INSERT INTO accounts VALUES(3,'Rails', 'demo',   2, 1, 0);
INSERT INTO accounts VALUES(4,'John', 'demo',    2, 0, 2);

INSERT INTO followships VALUES(1,1,2,'2009-06-16');
INSERT INTO followships VALUES(2,1,3,'2009-06-16');
INSERT INTO followships VALUES(3,4,1,'2009-07-17');
INSERT INTO followships VALUES(4,4,2,'2009-07-17');

INSERT INTO tweets (account_id, message, created_at) VALUES(1,'Scooter is amazing','2008-07-16');
INSERT INTO tweets (account_id, message, created_at) VALUES(1,'Scooter is fun','2007-06-16');
INSERT INTO tweets (account_id, message, created_at) VALUES(2,'Java programming is fun','2009-05-16');
INSERT INTO tweets (account_id, message) VALUES(3,'Rails is easy');
INSERT INTO tweets (account_id, message) VALUES(3,'Rails is fun');
INSERT INTO tweets (account_id, message, created_at) VALUES(4,'John is coding','2008-05-16');
INSERT INTO tweets (account_id, message, created_at) VALUES(4,'John is eating','2009-06-16');

