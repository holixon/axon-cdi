# Axon CDI

[![travis](https://travis-ci.org/holisticon/axon-cdi.svg?branch=master)](https://travis-ci.org/holisticon/axon-cdi)
[![codecov](https://codecov.io/gh/holisticon/axon-cdi/branch/master/graph/badge.svg)](https://codecov.io/gh/holisticon/axon-cdi)

[![Quality Gate](https://sonarqube.com/api/badges/gate?key=org.axonframework.extension:axon-cdi-root)](https://sonarcloud.io/dashboard?id=org.axonframework.extension%3Aaxon-cdi-root)
[![Sonar Rating](https://sonarqube.com/api/badges/measure?key=org.axonframework.extension:axon-cdi-root&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=org.axonframework.extension%3Aaxon-cdi-root)
[![Sonar Debt Ration](https://sonarqube.com/api/badges/measure?key=org.axonframework.extension:axon-cdi-root&metric=sqale_debt_ratio)](https://sonarcloud.io/dashboard?id=org.axonframework.extension%3Aaxon-cdi-root)
[![Sonar Reliability](https://sonarqube.com/api/badges/measure?key=org.axonframework.extension:axon-cdi-root&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=org.axonframework.extension%3Aaxon-cdi-root)


CDI Extension to use AxonFramework 3.x in Container Environments

## Usage

Simply add the following dependency in your Apache Maven `pom.xml`:

      <dependency>
        <groupId>org.axonframework.extension</groupId>
        <artifactId>axon-cdi</artifactId>
        <version>0.0.1-SNAPSHOT</version>
      </dependency>

### AxonFramework Building Blocks

Axon CDI provides easy access to most important AxonFramework building blocks. These can simply be injected in your code:

 - CommandBus
 - CommandGateway
 - EventBus
 - Serializer
 - Configuration
  
### Aggregates

Similar to Axon Spring, you can define aggregate roots by putting a simple annotation `org.axonframework.cdi.stereotype.Aggregate` on your class. 
It will be automatically collected by the CDI container and registered (Don't forget the `beans.xml`).

## Examples

Please have a look on examples in `example` folder.

## Advanced usage

### Usage of JPA event store inside container

If you want to use the JPA based event store inside of a container (e.g. JBoss or Wildfly), you have to configure the following facilities:

  *  EntityManagerProvider
  *  TransactionManager
  *  EventStorageEngine
  *  TokenStore

   
