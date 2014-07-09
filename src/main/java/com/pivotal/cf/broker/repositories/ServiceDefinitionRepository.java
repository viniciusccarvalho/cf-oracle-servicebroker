package com.pivotal.cf.broker.repositories;

import org.springframework.data.repository.CrudRepository;

import com.pivotal.cf.broker.model.ServiceDefinition;

public interface ServiceDefinitionRepository extends CrudRepository<ServiceDefinition, String> {

}
