-- creating new user sql script

CREATE USER User_Module5 IDENTIFIED BY AdminPass123;

-- granting the necessary permissions to the new user
GRANT CONNECT, RESOURCE TO User_Module5;
GRANT CREATE TABLE TO User_Module5;
GRANT CREATE SEQUENCE TO User_Module5;
GRANT CREATE VIEW TO User_Module5;
GRANT CREATE SESSION TO User_Module5;
GRANT CREATE PROCEDURE TO User_Module5;
GRANT CREATE MATERIALIZED VIEW TO User_Module5;
GRANT UNLIMITED TABLESPACE to User_Module5;
