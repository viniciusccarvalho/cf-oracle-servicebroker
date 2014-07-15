CREATE USER ${binding.credentials.username} IDENTIFIED BY ${binding.credentials.password}
DEFAULT TABLESPACE ${instance.config.tablespace} 
TEMPORARY TABLESPACE ${instance.config.tablespace}_temp
PROFILE plan_${plan.name};

grant CREATE SESSION, ALTER SESSION, CREATE DATABASE LINK,
      CREATE MATERIALIZED VIEW, CREATE PROCEDURE, CREATE PUBLIC SYNONYM,
      CREATE ROLE, CREATE SEQUENCE, CREATE SYNONYM, CREATE TABLE, 
      CREATE TRIGGER, CREATE TYPE, CREATE VIEW, UNLIMITED TABLESPACE 
      to ${binding.credentials.username}