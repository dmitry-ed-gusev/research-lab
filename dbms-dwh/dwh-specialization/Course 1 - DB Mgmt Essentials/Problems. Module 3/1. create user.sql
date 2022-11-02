-- creating new user sql script

CREATE USER User_Module3 IDENTIFIED BY AdminPass123;

-- granting the necessary permissions to the new user
GRANT CONNECT, RESOURCE TO User_Module3;
GRANT CREATE TABLE TO User_Module3;
GRANT CREATE SEQUENCE TO User_Module3;
GRANT CREATE VIEW TO User_Module3;
GRANT CREATE SESSION TO User_Module3;
GRANT CREATE PROCEDURE TO User_Module3;
GRANT CREATE MATERIALIZED VIEW TO User_Module3;
GRANT UNLIMITED TABLESPACE to User_Module3;
