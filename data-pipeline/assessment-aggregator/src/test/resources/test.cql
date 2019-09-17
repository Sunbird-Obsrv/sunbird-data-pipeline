CREATE KEYSPACE sunbird_courses with replication = {'class':'SimpleStrategy','replication_factor':1};

CREATE TABLE sunbird_courses.assessment_aggregator (
course_id text,
batch_id text,
content_id text,
attempt_id text,
user_id text,
created_on timestamp,
last_attempted_on timestamp,
max_score int,
question list<frozen<question>>,
total_score int,
updated_on timestamp,
PRIMARY KEY (course_id, batch_id, content_id, attempt_id, user_id)
);

CREATE TYPE sunbird_courses.question (
    id text,
    max_score int,
    score int,
    type text,
    title text,
    resvalues list<frozen<map<text, text>>>,
    params list<frozen<map<text, text>>>,
    description text,
    duration decimal
);
