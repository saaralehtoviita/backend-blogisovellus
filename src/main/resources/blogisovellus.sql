DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS KEYWORDS CASCADE;
DROP TABLE IF EXISTS POSTS CASCADE;
DROP TABLE IF EXISTS POST_KEYWORD CASCADE;

CREATE TABLE USERS (
user_id BIGSERIAL PRIMARY KEY,
first_name VARCHAR(250) NOT NULL,
last_name VARCHAR(250) NOT NULL,
user_name VARCHAR(250) NOT NULL UNIQUE,
password_hashed VARCHAR(250),
app_role VARCHAR(250)
);

CREATE TABLE KEYWORDS (
keyword_id BIGSERIAL PRIMARY KEY,
str_keyword VARCHAR(250)
);

CREATE TABLE POSTS (
post_id BIGSERIAL PRIMARY KEY,
title VARCHAR(250) NOT NULL,
post_text TEXT NOT NULL,
post_date VARCHAR(250) NOT NULL,
user_id BIGINT REFERENCES USERS(user_id)
);

CREATE TABLE POST_KEYWORD (
id BIGSERIAL PRIMARY KEY, 
post_id BIGINT REFERENCES POSTS(post_id),
keyword_id BIGINT REFERENCES KEYWORDS(keyword_id)
);

INSERT INTO USERS (first_name, last_name, user_name, password_hashed, app_role)
VALUES('Testi', 'Testinen', 'user', '$2a$10$91d8dtRJvcEbqonif/vtKuod24Sudu4OVfLBa5muyDaiaDLRl5F9i', 'USER'),
('Admintesti', 'AdminTestinen', 'admin', '$2a$10$v9iDxguTwkaQm8utbKaCLuwxir/UNJj7sgUUFHOAdCEDuMqlp7MUC', 'ADMIN'),
('Elli', 'Esimerkki', 'elliuser', '$2a$10$PHKydfpA9E.1YzZcpMXMdOfiMqItqFFNIjvh1iwy3u1pm/akXp4o6', 'USER');

select * from USERS;

INSERT INTO KEYWORDS (str_keyword) 
VALUES('knee'),
('shoulder'),
('crossfit'), 
('ankle'), 
('injury'), 
('rehabilitation'), 
('injury prevention');

select * from KEYWORDS;

select * from USERS;

INSERT INTO POSTS (title, post_text, post_date, user_id)
VALUES('Injuries in Crosssfit', 'Blog text 1.', '23.9.2025', 3),
('Shoulder rehabilitation', 'Blog text 2.', '24.9.2025', 1),
('Ankle rehabilitation', 'Blog text 3', '25.9.2025', 3),
('Polvivammat', 'Blog text 4', '26.9.2025', 1),
('How to prevent injuries in Crossfit?', 'Blog text 5', '25.9.2025', 3);

select * from POSTS;
select * from KEYWORDS;

INSERT INTO POST_KEYWORD (post_id, keyword_id) 
VALUES(1, 3), (1, 5), 
(2, 3), (2, 2), (2, 6),
(3, 3), (3, 4), (3, 6),
(4, 5), (4, 1),
(5, 3), (5, 5), (5, 7);


select * from POSTS where post_id IN (
select post_id from POST_KEYWORD where keyword_id=(
select keyword_id from KEYWORDS where str_keyword='crossfit'
)
);

select * from posts;

select * from users;
