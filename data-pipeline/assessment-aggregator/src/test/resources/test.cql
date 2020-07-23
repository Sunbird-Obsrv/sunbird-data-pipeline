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

