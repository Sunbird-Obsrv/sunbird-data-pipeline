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
    locationids
)VALUES ('user-3', 'VOS','sunbird', 'IN','MANJU','24-apr-2020','24-apr-2020','24-08-1993','manjunathd@ilimi.in',true,'Manjunath', 1, 'Male','Davanam','Banglore','********gmail.com','******181', '*******','4682647342',true, '01285019302823526477', ['location-2', 'location-3', 'location-4']);

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

CREATE TABLE sunbird.user_org (
    id text PRIMARY KEY,
    addedby text,
    addedbyname text,
    approvaldate text,
    approvedby text,
    hashtagid text,
    isapproved boolean,
    isdeleted boolean,
    isrejected boolean,
    organisationid text,
    orgjoindate text,
    orgleftdate text,
    position text,
    roles list<text>,
    updatedby text,
    updateddate text,
    userid text
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
CREATE INDEX inx_uorg_userid ON sunbird.user_org (userid);
CREATE INDEX inx_uorg_orgid ON sunbird.user_org (organisationid);

    INSERT INTO sunbird.user_org (userid,id,organisationid) VALUES ('user-4','01267670270554112010', '0123653943740170242');
    INSERT INTO sunbird.user_org (userid,id,organisationid) VALUES ('user-4','012676702705541120109', '0125648776347320320');


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

INSERT INTO sunbird.organisation (id,channel, isrootorg, locationids, orgcode, orgname) VALUES ('0123653943740170242','ROOT_ORG', False, ['location-2', 'location-3', 'location-4'], 'Org-1', 'QA ORG');
INSERT INTO sunbird.organisation (id,channel, isrootorg, locationids, orgcode, orgname) VALUES ('0125648776347320320','ROOT_ORG', False, ['location-2', 'location-3', 'location-4'], 'Org-2', 'Sunbird ORG');

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

    INSERT INTO sunbird.usr_external_identity (userid,idtype,originalprovider, externalid, provider) VALUES ('user-3','declared-ext-id', 'apekx', '93nsoa01', 'apekx');
    INSERT INTO sunbird.usr_external_identity (userid,idtype,originalprovider, externalid, provider) VALUES ('user-3','declared-school-name', 'apekx', 'SBA School', 'apekx');
    INSERT INTO sunbird.usr_external_identity (userid,idtype,originalprovider, externalid, provider) VALUES ('user-3','declared-school-udise-code', 'apekx', '038sba937', 'apekx');
    INSERT INTO sunbird.usr_external_identity (userid,idtype,originalprovider, externalid, provider) VALUES ('user-3','declared-state', 'apekx', 'Karnataka', 'apekx');
    INSERT INTO sunbird.usr_external_identity (userid,idtype,originalprovider, externalid, provider) VALUES ('user-3','declared-district', 'apekx', 'Tumkur', 'apekx');
    INSERT INTO sunbird.usr_external_identity (userid,idtype,originalprovider, externalid, provider) VALUES ('user-3','declared-phone', 'apekx', '847389758393', 'apekx');
    INSERT INTO sunbird.usr_external_identity (userid,idtype,originalprovider, externalid, provider) VALUES ('user-4','apekx', 'apekx', '948ext1', 'apekx');

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