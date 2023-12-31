-- creating new user sql script

CREATE USER StoreDBA IDENTIFIED BY AdminPass123;

-- granting the necessary permissions to the new user
GRANT CONNECT, RESOURCE TO StoreDBA;
GRANT CREATE TABLE TO StoreDBA;
GRANT CREATE SEQUENCE TO StoreDBA;
GRANT CREATE VIEW TO StoreDBA;
GRANT CREATE SESSION TO StoreDBA;
GRANT CREATE PROCEDURE TO StoreDBA;
GRANT CREATE MATERIALIZED VIEW TO StoreDBA;
GRANT UNLIMITED TABLESPACE to StoreDBA;
