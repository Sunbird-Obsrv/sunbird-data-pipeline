CREATE TABLE IF NOT EXISTS {{ env }}_geo_location_city_temp(geoname_id INTEGER UNIQUE, locale_code VARCHAR(3), continent_code VARCHAR(3), continent_name VARCHAR(100), country_iso_code VARCHAR(5), country_name VARCHAR(100), subdivision_1_iso_code VARCHAR(50), subdivision_1_name VARCHAR(100), subdivision_2_iso_code VARCHAR(50), subdivision_2_name VARCHAR(100), city_name VARCHAR(100), metro_code VARCHAR(10), time_zone VARCHAR(50), is_in_european_union SMALLINT);

CREATE TABLE IF NOT EXISTS {{ env }}_geo_location_city_ipv4_temp(network_start_integer BIGINT, network_last_integer BIGINT, geoname_id INTEGER, registered_country_geoname_id INTEGER, represented_country_geoname_id INTEGER, is_anonymous_proxy SMALLINT, is_satellite_provider SMALLINT, postal_code VARCHAR(50), latitude NUMERIC(9, 6), longitude NUMERIC(9, 6), accuracy_radius SMALLINT);

\COPY {{ env }}_geo_location_city_temp FROM '{{ maxmind_db_download_dir }}/{{ maxmind_db_dir_name }}/{{ maxmind_db_geo_city_locations_filename }}' WITH CSV HEADER DELIMITER ',';

\COPY {{ env }}_geo_location_city_ipv4_temp FROM '{{ maxmind_db_download_dir }}/{{ maxmind_db_dir_name }}/{{ maxmind_db_geo_city_ip_range_filename }}' WITH CSV HEADER DELIMITER ',';

CREATE TABLE IF NOT EXISTS {{ env }}_maxmind_custom_data_mapping(geoname_id INTEGER UNIQUE, subdivision_1_custom_code VARCHAR(50), subdivision_1_custom_name VARCHAR(100), subdivision_2_custom_code VARCHAR(50), subdivision_2_custom_name VARCHAR(100));

\COPY {{ env }}_maxmind_custom_data_mapping FROM '{{ maxmind_db_download_dir }}/maxmind_custom_data_mapping.csv' WITH CSV HEADER DELIMITER ',';

CREATE TABLE IF NOT EXISTS {{ env }}_geo_location_city(geoname_id INTEGER UNIQUE, locale_code VARCHAR(3), continent_code VARCHAR(3), continent_name VARCHAR(100), country_iso_code VARCHAR(5), country_name VARCHAR(100), subdivision_1_iso_code VARCHAR(50), subdivision_1_name VARCHAR(100), subdivision_2_iso_code VARCHAR(50), subdivision_2_name VARCHAR(100), city_name VARCHAR(100), metro_code VARCHAR(10), time_zone VARCHAR(50), is_in_european_union SMALLINT, subdivision_1_custom_code VARCHAR(50), subdivision_1_custom_name VARCHAR(100), subdivision_2_custom_code VARCHAR(50), subdivision_2_custom_name VARCHAR(100));

CREATE TABLE IF NOT EXISTS {{ env }}_geo_location_city_ipv4(network_start_integer BIGINT, network_last_integer BIGINT, geoname_id INTEGER, registered_country_geoname_id INTEGER, represented_country_geoname_id INTEGER, is_anonymous_proxy SMALLINT, is_satellite_provider SMALLINT, postal_code VARCHAR(50), latitude NUMERIC(9, 6), longitude NUMERIC(9, 6), accuracy_radius SMALLINT);

CREATE INDEX CONCURRENTLY IF NOT EXISTS ind_geoname_id ON {{ env }}_geo_location_city_ipv4 (geoname_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS ind_network_start_integer ON {{ env }}_geo_location_city_ipv4 (network_start_integer);

CREATE INDEX CONCURRENTLY IF NOT EXISTS ind_network_last_integer ON {{ env }}_geo_location_city_ipv4 (network_last_integer);

CREATE INDEX CONCURRENTLY IF NOT EXISTS ind_country_iso_code ON {{ env }}_geo_location_city (country_iso_code);

TRUNCATE TABLE {{ env }}_geo_location_city_ipv4 CASCADE;

TRUNCATE TABLE {{ env }}_geo_location_city CASCADE;

INSERT INTO {{ env }}_geo_location_city SELECT glc.geoname_id, glc.locale_code, glc.continent_code, glc.continent_name, glc.country_iso_code, glc.country_name, glc.subdivision_1_iso_code, glc.subdivision_1_name, glc.subdivision_2_iso_code, glc.subdivision_2_name, glc.city_name, glc.metro_code, glc.time_zone, glc.is_in_european_union, mcd.subdivision_1_custom_code, mcd.subdivision_1_custom_name, mcd.subdivision_2_custom_code, mcd.subdivision_2_custom_name FROM {{ env }}_geo_location_city_temp glc LEFT JOIN {{ env }}_maxmind_custom_data_mapping mcd ON glc.geoname_id = mcd.geoname_id WHERE glc.country_iso_code = 'IN';

INSERT INTO {{ env }}_geo_location_city_ipv4 SELECT gip.* FROM {{ env }}_geo_location_city glc, {{ env }}_geo_location_city_ipv4_temp gip WHERE glc.geoname_id = gip.geoname_id;

DROP TABLE {{ env }}_geo_location_city_temp CASCADE;

DROP TABLE {{ env }}_geo_location_city_ipv4_temp CASCADE;

DROP TABLE {{ env }}_maxmind_custom_data_mapping CASCADE;
