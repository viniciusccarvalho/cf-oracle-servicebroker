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

The broker controls resources on Oracle. Much like the mysql broker, the broker can control two resources: table sizes and number of connections. It does that by
controlling PROFILES and TABLESPACES on Oracle.

It uses metadata from the plan definition to do so. There's two fields that you can use to control resources:

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
You can define (for now) max_size and connections.

### Creating a plan

When you create a new plan, a new PROFILE is created on Oracle defining the max_number_sessions, it uses the `connections` parameter to do so.

### Creating an instance

When a new instance is created, a new TABLESPACE and a TEMPORARY TABLESPACE is created using the max_size parameter.

### Binding an instance

Finally when you bind the service, a new USER is created using the PROFILE defined by the plan, and using the TABLESPACE created by the instance.

## Installing

You will need to download the oc4j jar and install manually, its configured on build.gradle as version 12.6.6.6 (obvious reasons). Just install the dependency
on your local maven.

Push the app to CF. Create some plans, and then just follow the tutorial on [Managing Service Brokers](http://docs.cloudfoundry.org/services/managing-service-brokers.html) to register it.


