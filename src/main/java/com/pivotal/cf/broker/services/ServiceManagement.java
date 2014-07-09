package com.pivotal.cf.broker.services;

import java.util.List;

import com.pivotal.cf.broker.model.CreateServiceInstanceRequest;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;
import com.pivotal.cf.broker.model.ServiceInstanceBindingRequest;

public interface ServiceManagement {
	
	public ServiceInstance createInstance(CreateServiceInstanceRequest serviceRequest);
	
	public boolean removeServiceInstance(String serviceInstanceId);
	
	public List<ServiceInstance> listInstances();
	
	public ServiceInstanceBinding createInstanceBinding(ServiceInstanceBindingRequest bindingRequest);
	
	public boolean removeBinding(String serviceBindingId);
	
	public List<ServiceInstanceBinding> listBindings();
	
}
