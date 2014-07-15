# Oracle Service Broker

Cloudfoundry service broker for Oracle databases.

`WORK IN PROGRESS NOT READY, FEW BUGS, NEEDS BETTER DOCUMENTATION AND REFACTORING. PULL REQUESTS WELCOME`	

This simple service broker is meant to demonstrate how to create a service broker that manages a service not managed by BOSH with cloudfoundry.

##Setting up

This is a [spring-boot](http://projects.spring.io/spring-boot/) application and it follows many of its principles.

The broker needs two connections to your oracle database. One needs to be a SYSDBA type of user, that can create tablespaces, users, profiles. The
other connection is just a regular user that can create tables to store the metadata of the broker (plans, instances, bindings)

The config file is located at `src/main/resources/config/application.properties`

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

When the application starts it will create the first ServiceDefinition based on a template found at `src/main/resources/ServiceDescription.json`, you can 
adjust to your preferences there.

## Extensions to CF Service Broker API

Besides the endpoints defined on [Service Broker API](http://docs.cloudfoundry.org/services/api.html) the broker adds a few more to allow creation of services and plans:

`POST /v2/catalog/services` - Creates a new ServiceDefinition

The payload is the same as the one found on the [Service Broker API](http://docs.cloudfoundry.org/services/api.html)

```
{
"name" : "Oracle XE",
"description" : "Oracle Express Database Service",
"bindable" : true,
"tags" : ["oracle","relational"],
"metadata" : {
"longDescription" : "Experimental Oracle XE Service. New services create new tablespaces, Plans defines tablespaces and session quotas. Bindings create users",
"imageUrl" : "https://raw.githubusercontent.com/viniciusccarvalho/cf-oracle-servicebroker/master/src/main/resources/static/images/oracle_icon.jpg"
}
}
```

`DELETE /v2/catalog/services/{serviceDefinitionId}` - Removes a service definition. Throws an error if there's plans associated with it

`POST /v2/catalog/services/{serviceDefinitionId}/plans` - Adds a new plan

`DELETE /v2/catalog/services/{serviceDefinitionId}/plans/{planId}` - Removes a new plan. Throws an error if there's instances using this plan

##How does it work

The broker controls resources on Oracle. Much like the mysql broker. The broker uses a template system to define what to create inside oracle for each operation.

Templates are located under /src/resources/templates. There's 3 templates directories (plan,instance,binding) each represents one action that the broker support.

Inside those folders templates are named (create.ftl -> POST/PUT of resource, delete.ftl -> DELETE of resource)

You can customize the behavior of the broker by changing those templates, without having to touch the broker code.

 

### Creating a plan

When you create a plan, the broker will bind the template with the plan JSON object that you used to create the plan. For example:

```

{
"name" : "dev",
"description" : "development plan",
"metadata" : {
"max_size" : "250M",
"connections" : 5,
"bullets" : ["250 megabytes of space","5 simultaneous connections"]
}
}

```

```

CREATE PROFILE plan_${plan.name} LIMIT SESSIONS_PER_USER ${plan.metadata.other.connections}

```

As you can see, creating a plan creates a new oracle profile, you can use any metadata.other attribute to pass to your SQL definition.

### Creating an instance

When you create an instance the broker will provide both the full plan JSON object plus an instance object. The instance object has a metadata named
tablespace which is a random string that can be used to create the tablespace for the instance

```
create tablespace ${instance.config.tablespace} 
datafile '${instance.config.tablespace}.dat' 
size 10M 
autoextend on 
maxsize ${plan.metadata.other.max_size} 
extent management local 
uniform size 64K;

create temporary tablespace ${instance.config.tablespace}_temp 
tempfile '${instance.config.tablespace}_temp.dat'
size 10M 
autoextend on next 32m 
maxsize ${plan.metadata.other.max_size}
extent management local
```

### Binding an instance

At last when you bind an instance, the broker provides the plan, instance and binding objects to your template:

The binding.credentials will contain a random username/password that the service creates.
```
CREATE USER ${binding.credentials.username} IDENTIFIED BY ${binding.credentials.password}
DEFAULT TABLESPACE ${instance.config.tablespace} 
TEMPORARY TABLESPACE ${instance.config.tablespace}_temp
PROFILE plan_${plan.name};

grant CREATE SESSION, ALTER SESSION, CREATE DATABASE LINK,
      CREATE MATERIALIZED VIEW, CREATE PROCEDURE, CREATE PUBLIC SYNONYM,
      CREATE ROLE, CREATE SEQUENCE, CREATE SYNONYM, CREATE TABLE, 
      CREATE TRIGGER, CREATE TYPE, CREATE VIEW, UNLIMITED TABLESPACE 
      to ${binding.credentials.username}
```
## Installing

You will need to download the oc4j jar and install manually, its configured on build.gradle as version 12.6.6.6 (obvious reasons). Just install the dependency
on your local maven.

Push the app to CF. Create some plans, and then just follow the tutorial on [Managing Service Brokers](http://docs.cloudfoundry.org/services/managing-service-brokers.html) to register it.


