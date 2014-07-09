# Oracle Service Broker

Cloudfoundry service broker for Oracle databases.	

This simple service broker is meant to demonstrate how to create a service broker that manages a service not managed by BOSH with cloudfoundry.

##Setting up

This is a spring-boot application and it follows many of its principles.

The broker needs two connections to your oracle database. One needs to be a SYSDBA type of user, that can create tablespaces, users, profiles. The
other connection is just a regular user that can create tables to store the metadata of the broker (plans, instances, bindings)

The config file is located at *src/main/resources/config/application.properties*

```
oracle.host=localhost
oracle.port=1521
oracle.service=xe
spring.jpa.hibernate.ddl-auto=create-update
#application connection info
broker.datasource.driverClassName=oracle.jdbc.OracleDriver
broker.datasource.url=jdbc:oracle:thin:@${oracle.host}:${oracle.port}:${oracle.service}
broker.datasource.username=servicebroker
broker.datasource.password=s3cr3t
broker.datasource.max-active=5
broker.datasource.max-idle=2
broker.datasource.min-idle=1
broker.datasource.initial-size=1
#DBA connection info
dba.datasource.driverClassName=oracle.jdbc.OracleDriver
dba.datasource.url=jdbc:oracle:thin:@${oracle.host}:${oracle.port}:${oracle.service}
dba.datasource.username=system
dba.datasource.password=passw0rd
dba.datasource.max-active=5
dba.datasource.max-idle=2
dba.datasource.min-idle=1
dba.datasource.initial-size=1

```

Change the connection information for your database here.