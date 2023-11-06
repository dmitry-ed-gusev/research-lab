-- creating new user sql script

CREATE USER inventorydw IDENTIFIED BY inventorydw;

-- granting the necessary permissions to the new user
GRANT CONNECT, RESOURCE TO inventorydw;
GRANT CREATE TABLE TO inventorydw;
GRANT CREATE SEQUENCE TO inventorydw;
GRANT CREATE VIEW TO inventorydw;
GRANT CREATE SESSION TO inventorydw;
GRANT CREATE PROCEDURE TO inventorydw;
GRANT CREATE MATERIALIZED VIEW TO inventorydw;
GRANT UNLIMITED TABLESPACE to inventorydw;
