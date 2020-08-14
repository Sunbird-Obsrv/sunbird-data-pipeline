CREATE KEYSPACE sunbird WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};

CREATE TABLE sunbird.user (
    id text PRIMARY KEY,
    avatar text,
    channel text,
    countrycode text,
    createdby text,
    createddate text,
    currentlogintime text,
    dob text,
    email text,
    emailverified boolean,
    firstname text,
    flagsvalue int,
    framework map<text, frozen<list<text>>>,
    gender text,
    grade list<text>,
    isdeleted boolean,
    language list<text>,
    lastlogintime text,
    lastname text,
    location text,
    locationids list<text>,
    loginid text,
    maskedemail text,
    maskedphone text,
    password text,
    phone text,
    phoneverified boolean,
    prevusedemail text,
    prevusedphone text,
    profilesummary text,
    profilevisibility map<text, text>,
    provider text,
    recoveryemail text,
    recoveryphone text,
    registryid text,
    roles list<text>,
    rootorgid text,
    status int,
    subject list<text>,
    tcstatus text,
    tcupdateddate text,
    temppassword text,
    thumbnail text,
    tncacceptedon timestamp,
    tncacceptedversion text,
    updatedby text,
    updateddate text,
    userid text,
    username text,
    usertype text,
    webpages list<frozen<map<text, text>>>
) WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';
CREATE INDEX inx_u_userid ON sunbird.user (userid);
CREATE INDEX inx_u_email ON sunbird.user (email);
CREATE INDEX inx_u_loginid ON sunbird.user (loginid);
CREATE INDEX inx_u_status ON sunbird.user (status);
CREATE INDEX inx_u_username ON sunbird.user (username);
CREATE INDEX inx_u_phone ON sunbird.user (phone);

INSERT INTO sunbird.user (
    id,
    avatar,
    channel ,
    countrycode,
    createdby,
    createddate,
    currentlogintime,
    dob,
    email,
    emailverified,
    firstname,
    flagsvalue,
    gender,
    lastname,
    location,
    maskedemail,
    maskedphone,
    password,
    phone,
    phoneverified
)
VALUES ('user-3', 'VOS','sunbird', 'IN','MANJU','24-apr-2020','24-apr-2020','24-08-1993','manjunathd@ilimi.in',true,'Manjunath', 1, 'Male','Davanam','Banglore','********gmail.com','******181', '*******','4682647342',true);

CREATE TABLE sunbird.location (
    id text PRIMARY KEY,
    code text,
    name text,
    parentid text,
    type text
);

CREATE INDEX inx_loc_code ON sunbird.location (code);

INSERT INTO sunbird.location(id,code,name,type) VALUES ('location-1','0f5f0702000f6', 'KARNATAKA', 'state');
INSERT INTO sunbird.location(id,code,name,type) VALUES ('location-2','g0507020f00f6', 'TUMKUR', 'district');
INSERT INTO sunbird.location(id,code,name,type) VALUES ('location-3','331211','MANVI','block');