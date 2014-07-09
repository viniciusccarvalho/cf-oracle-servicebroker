package com.pivotal.cf.broker.services;

import com.pivotal.cf.broker.model.CreateServiceInstanceRequest;
import com.pivotal.cf.broker.model.Plan;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;

public interface DatabaseService {
	public boolean createProfile(Plan plan);
	public boolean createTableSpaces(ServiceInstance instance);
	public boolean deleteTableSpaces(String serviceInstanceId);
	
	public boolean createUser(ServiceInstanceBinding binding);
	public boolean deleteUser(ServiceInstanceBinding binding);
}
