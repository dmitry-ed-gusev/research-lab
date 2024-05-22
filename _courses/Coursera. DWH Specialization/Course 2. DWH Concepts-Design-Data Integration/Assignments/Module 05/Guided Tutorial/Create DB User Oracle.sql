-- creating new user sql script

CREATE USER sstoretest IDENTIFIED BY sstoretest;

-- granting the necessary permissions to the new user
GRANT CONNECT, RESOURCE TO sstoretest;
GRANT CREATE TABLE TO sstoretest;
GRANT CREATE SEQUENCE TO sstoretest;
GRANT CREATE VIEW TO sstoretest;
GRANT CREATE SESSION TO sstoretest;
GRANT CREATE PROCEDURE TO sstoretest;
GRANT CREATE MATERIALIZED VIEW TO sstoretest;
GRANT UNLIMITED TABLESPACE to sstoretest;
