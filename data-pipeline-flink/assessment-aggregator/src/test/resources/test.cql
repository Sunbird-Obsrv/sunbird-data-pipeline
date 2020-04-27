CREATE KEYSPACE sunbird_courses with replication = {'class':'SimpleStrategy','replication_factor':1};


CREATE TYPE IF NOT EXISTS sunbird_courses.question (
id text,
assess_ts timestamp,
max_score double,
score double,
type text,
title text,
resvalues list<frozen<map<text, text>>>,
params list<frozen<map<text, text>>>,
description text,
duration decimal
);


CREATE TABLE IF NOT EXISTS sunbird_courses.assessment_aggregator (
course_id text,
batch_id text,
content_id text,
attempt_id text,
user_id text,
created_on timestamp,
last_attempted_on timestamp,
total_max_score double,
grand_total text,
question list<frozen<question>>,
total_score double,
updated_on timestamp,
PRIMARY KEY (course_id, batch_id, content_id, attempt_id, user_id)
);

CREATE INDEX assessment_aggregator_last_attempted_on_idx ON sunbird_courses.assessment_aggregator (last_attempted_on);


/*INSERT INTO sunbird_courses.assessment_aggregator (course_id,batch_id,user_id,content_id,attempt_id,created_on,grand_total
,last_attempted_on,question,total_max_score,total_score,updated_on) VALUES
('do_11287774806987571218','0128777534861066242','8454cb21-3ce9-4e30-85b5-fade097880d8','do_112876961957437440179',
'0afe2cc175ffc95b83a4dc8d1af2c47b','2019-11-07 05:38:49.640+0000','2/5','2019-11-07 05:38:05.076+0000',null,
5,2,'2019-11-07 05:38:49.642+0000');*/

