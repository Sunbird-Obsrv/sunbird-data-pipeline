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
    phoneverified,
    rootorgid,
    locationids,
    framework
)VALUES ('user-3', 'VOS','sunbird', 'IN','MANJU','24-apr-2020','24-apr-2020','24-08-1993','manjunathd@ilimi.in',true,'Manjunath', 1, 'Male','Davanam','Banglore','********gmail.com','******181', '*******','4682647342',true, '01285019302823526477', ['location-2', 'location-3', 'location-4'], {'board': ['State (Madhya Pradesh)'], 'gradeLevel': ['Class 11', 'Class 12'], 'id': ['mp_k-12'], 'medium': ['Hindi'], 'subject': ['Hindi']});

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
    phoneverified,
    rootorgid,
    locationids
)VALUES ('user-4', 'VOS','apekx', 'IN','UTKARSHA','04-jul-2020','04-jul-2020','28-10-1996','utkarsha@ilimi.in',true,'Utkarsha', 1, 'Female','Kapoor','Banglore','********gmail.com','******345', '*******','4682647342',true, '01285019302823526479', ['location-2', 'location-3', 'location-4']);

INSERT INTO sunbird.user (id, userid, rootorgid, firstname, lastname) VALUES ('user-9', 'user-9', '01285019302823526477', 'UT', 'KAPOOR');
INSERT INTO sunbird.user (id, userid, rootorgid, firstname, lastname) VALUES ('user-6', 'user-6', '01285019302823526477', 'Revathi', 'Kotla');
INSERT INTO sunbird.user (
    id,
    avatar,
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
    phoneverified,
    rootorgid,
    locationids
)VALUES ('user-11', 'VOS', 'IN','SOWMYA','13-jul-2020','13-jul-2020','28-10-1996','sowmyad@ilimi.in',true,'Sowmya', 1, 'Female','Dixit','Banglore','********gmail.com','******358', '*******','4682647342',true, '01285019302823526480', ['location-2', 'location-3']);

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
    phoneverified,
    rootorgid,
    locationids,
    framework
)VALUES ('user-12', 'VOS','sunbird', 'IN','ISHA','29-apr-2020','29-apr-2020','24-08-1993','isha@ilimi.in',true,'Isha', 1, 'Male','Wakankar','Jhansi','********gmail.com','******181', '*******','4682647342',true, '01285019302823526477', ['location-2', 'location-3', 'location-4'], {'board': ['State (Madhya Pradesh)'], 'gradeLevel': ['Class 11', 'Class 12'], 'id': ['mp_k-12'], 'medium': ['Hindi']});

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
    phoneverified,
    rootorgid,
    locationids,
    framework
)VALUES ('user-1', 'VOS','sunbird', 'IN','ANAND','24-apr-2020','24-apr-2020','24-08-1993','anandp@ilimi.in',true,'Anand', 1, 'Male','Parthasarthy','Banglore','********gmail.com','******121', '*******','4682647342',true, '01285019302823526477', ['location-2', 'location-3', 'location-4'], {'board': ['State (Madhya Pradesh)'], 'gradeLevel': ['Class 8', 'Class 12'], 'id': ['mp_k-12'], 'medium': ['Hindi'], 'subject': ['English']});

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
    phoneverified,
    rootorgid,
    locationids,
    framework
)VALUES ('user-5', 'VOS','sunbird', 'IN','UTKARSHA','24-sept-2020','24-sept-2020','24-08-1993','utk12@ilimi.in',true,'Utkarsha', 1, 'Female','Kapoor','Delhi','********gmail.com','******121', '*******','4682647342',true, '01285019302823526477', ['location-2', 'location-3', 'location-4'], {'board': ['State (Madhya Pradesh)'], 'gradeLevel': ['Class 8', 'Class 12'], 'id': ['mp_k-12'], 'medium': ['Hindi'], 'subject': ['English']});

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
    phoneverified,
    rootorgid,
    locationids,
    framework
)VALUES ('user-2', 'VOS','sunbird', 'IN','ANAND','24-apr-2020','24-apr-2020','24-08-1993','anandp@ilimi.in',true,'Anand', 1, 'Male','Parthasarthy','Banglore','********gmail.com','******121', '*******','4682647342',true, '01285019302823526477', ['location-2', 'location-3', 'location-4'], {'board': ['State (Madhya Pradesh)'], 'gradeLevel': ['Class 8', 'Class 12'], 'id': ['mp_k-12'], 'medium': ['Hindi'], 'subject': ['English']});

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
    phoneverified,
    locationids,
    framework
)VALUES ('user-14', 'VOS','sunbird', 'IN','Mahesh','29-apr-2020','29-apr-2020','24-08-1993','mahesh@ilimi.in',true,'Mahesh', 1, 'Male','Gangula','Banglore','********gmail.com','******121', '*******','4682647342',true, ['location-2', 'location-3', 'location-4'], {'board': ['State (Madhya Pradesh)'], 'gradeLevel': ['Class 8', 'Class 12'], 'id': ['mp_k-12'], 'medium': ['Hindi'], 'subject': ['English']});

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
    phoneverified,
    rootorgid,
    locationids,
    framework
)VALUES ('user-15', 'VOS','sunbird', 'IN','Harish','29-apr-2020','29-apr-2020','29-08-1993','harish@ilimi.in',true,'Harish', 1, 'Male','Gangula','Banglore','********gmail.com','******182', '*******','4682647342',true, '012850193028235264773', ['location-2', 'location-3', 'location-4'], {'board': ['State (Madhya Pradesh)'], 'gradeLevel': ['Class 11', 'Class 12'], 'id': ['mp_k-12'], 'medium': ['Hindi'], 'subject': ['Hindi']});

CREATE TABLE sunbird.location (
    id text PRIMARY KEY,
    code text,
    name text,
    parentid text,
    type text
);

CREATE INDEX inx_loc_code ON sunbird.location (code);

INSERT INTO sunbird.location(id,code,name,type) VALUES ('location-1','ft0507020006', 'KARNATAKA', 'state');
INSERT INTO sunbird.location(id,code,name,type) VALUES ('location-2','0f5f0702000f6', 'KARNATAKA', 'state');
INSERT INTO sunbird.location(id,code,name,type) VALUES ('location-3','g0507020f00f6', 'TUMKUR', 'district');
INSERT INTO sunbird.location(id,code,name,type) VALUES ('location-4','g0507020f00f6', 'MANVI', 'block');
INSERT INTO sunbird.location(id,code,name,type) VALUES ('location-5','g0507020f00f6', 'TAMIL NADU', 'state');
INSERT INTO sunbird.location(id,code,name,type) VALUES ('location-6','g0507020f00f6', 'VELLORE', 'district');

CREATE TABLE sunbird.user_organisation (
    userid text,
    organisationid text,
    addedby text,
    addedbyname text,
    approvaldate text,
    approvedby text,
    hashtagid text,
    id text,
    isapproved boolean,
    isdeleted boolean,
    isrejected boolean,
    orgjoindate text,
    orgleftdate text,
    position text,
    roles list<text>,
    updatedby text,
    updateddate text,
    PRIMARY KEY (userid, organisationid)
) WITH CLUSTERING ORDER BY (organisationid ASC)
    AND bloom_filter_fp_chance = 0.01
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

    INSERT INTO sunbird.user_organisation (userid,id,organisationid) VALUES ('user-4','01267670270554112010', '0123653943740170242');
    INSERT INTO sunbird.user_organisation (userid,id,organisationid) VALUES ('user-4','012676702705541120109', '0125648776347320320');
    INSERT INTO sunbird.user_organisation (userid,id,organisationid) VALUES ('user-15','012676702705541120109', '01256487763473203201');

CREATE TABLE sunbird.organisation (
    id text PRIMARY KEY,
    addressid text,
    approvedby text,
    approveddate text,
    channel text,
    communityid text,
    contactdetail text,
    createdby text,
    createddate text,
    datetime timestamp,
    description text,
    email text,
    externalid text,
    hashtagid text,
    homeurl text,
    imgurl text,
    isapproved boolean,
    isdefault boolean,
    isrootorg boolean,
    isssoenabled boolean,
    keys map<text, frozen<list<text>>>,
    locationid text,
    locationids list<text>,
    noofmembers int,
    orgcode text,
    orgname text,
    orgtype text,
    orgtypeid text,
    parentorgid text,
    preferredlanguage text,
    provider text,
    rootorgid text,
    slug text,
    status int,
    theme text,
    thumbnail text,
    updatedby text,
    updateddate text
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

INSERT INTO sunbird.organisation (id,channel, isrootorg, locationids, orgcode, orgname, externalid) VALUES ('0123653943740170242','ROOT_ORG', False, ['location-2', 'location-3', 'location-4'], 'Org-1', 'QA ORG', '097465');
INSERT INTO sunbird.organisation (id,channel, isrootorg, locationids, orgcode, orgname, externalid) VALUES ('0125648776347320320','ROOT_ORG', False, ['location-2', 'location-3', 'location-4'], 'Org-2', 'Sunbird ORG', '342586');
INSERT INTO sunbird.organisation (id,channel, isrootorg, locationids, orgcode, orgname, externalid) VALUES ('01285019302823526477','ROOT_ORG', True, ['location-2', 'location-3', 'location-4'], 'Org-2', 'Custodian ORG', '452463');
INSERT INTO sunbird.organisation (id,channel, isrootorg, locationids, orgcode, orgname, externalid) VALUES ('012850193028235264771','apekx', True, ['location-2', 'location-3', 'location-4'], 'Org-2', 'Custodian ORG', '235634');
INSERT INTO sunbird.organisation (id,channel, isrootorg, locationids, orgcode, orgname, externalid) VALUES ('01256487763473203201','up', False, ['location-2', 'location-3', 'location-4'], 'Org-2', 'Uttam Public School', '2356341');

CREATE TABLE sunbird.usr_external_identity (
    userid text,
    idtype text,
    provider text,
    createdby text,
    createdon timestamp,
    externalid text,
    lastupdatedby text,
    lastupdatedon timestamp,
    originalexternalid text,
    originalidtype text,
    originalprovider text,
    PRIMARY KEY (userid, idtype, provider)
) WITH CLUSTERING ORDER BY (idtype ASC, provider ASC)
    AND bloom_filter_fp_chance = 0.01
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

    INSERT INTO sunbird.usr_external_identity (userid,idtype,originalprovider, externalid, provider) VALUES ('user-4','01285019302823526479', '01285019302823526479', '948ext1', '01285019302823526479');

CREATE TABLE sunbird.system_settings (
     id text PRIMARY KEY,
     field text,
     value text
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
     CREATE INDEX inx_system_settings ON sunbird.system_settings (field);

     INSERT INTO sunbird.system_settings(id,field,value) VALUES ('custodianRootOrgId','custodianRootOrgId', '01285019302823526477');

CREATE TABLE sunbird.user_declarations (
     userid text,
     orgid text,
     persona text,
     createdby text,
     createdon timestamp,
     errortype text,
     status text,
     updatedby text,
     updatedon timestamp,
     userinfo map<text, text>,
     PRIMARY KEY (userid, orgid, persona)
) WITH CLUSTERING ORDER BY (orgid ASC, persona ASC)
     AND bloom_filter_fp_chance = 0.01
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

     INSERT INTO sunbird.user_declarations (userid,orgid,persona,userinfo) VALUES ('user-3','012850193028235264771', 'teacher',{'declared-school-name': 'SBA School', 'declared-school-udise-code': '038sba937', 'type': 'school', 'declared-ext-id': '93nsoa01'});
     INSERT INTO sunbird.user_declarations (userid,orgid,persona,userinfo) VALUES ('user-3','012850193028235264771', 'other',{'declared-school-name': 'School123', 'declared-school-udise-code': '038sba9371', 'type': 'school', 'declared-ext-id': '93nsoa0134'});
     INSERT INTO sunbird.user_declarations (userid,orgid,persona,userinfo) VALUES ('user-12','01285019302823526477', 'teacher',{'declared-ext-id': '93nsoa01'});
     INSERT INTO sunbird.user_declarations (userid,orgid,persona,userinfo) VALUES ('user-9','01285019302823526472', 'teacher',{'declared-ext-id': '93nsoa234', 'declared-school-udise-code': 'school001', 'declared-school-name': 'TPS School'});