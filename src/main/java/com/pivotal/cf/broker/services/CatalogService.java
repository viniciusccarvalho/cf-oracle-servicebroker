package com.pivotal.cf.broker.services;

import java.util.List;

import com.pivotal.cf.broker.model.CreateServiceInstanceRequest;
import com.pivotal.cf.broker.model.CreateServiceInstanceResponse;
import com.pivotal.cf.broker.model.Plan;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;

public interface CatalogService {

	public ServiceDefinition createServiceDefinition(ServiceDefinition serviceDefinition);
	public boolean deleteServiceDefinition(String serviceDefinitionId);
	
	public List<ServiceDefinition> listServices();
	
	
}
