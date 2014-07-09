package com.pivotal.cf.broker.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pivotal.cf.broker.exceptions.EntityNotFoundException;
import com.pivotal.cf.broker.model.CreateServiceInstanceRequest;
import com.pivotal.cf.broker.model.Plan;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.repositories.PlanRepository;
import com.pivotal.cf.broker.repositories.ServiceDefinitionRepository;
import com.pivotal.cf.broker.repositories.ServiceInstanceRepository;
import com.pivotal.cf.broker.services.BaseService;
import com.pivotal.cf.broker.services.CatalogService;
import com.pivotal.cf.broker.services.DatabaseService;

@Service
public class CatalogServiceImpl extends BaseService implements CatalogService{

	@Autowired
	private PlanRepository planRepository;
	
	@Autowired
	private ServiceDefinitionRepository serviceRepository;
	
	@Autowired 
	private ServiceInstanceRepository serviceInstanceRepository;
	
	@Autowired
	private DatabaseService dbService;
	
	

	@Override
	public ServiceDefinition createServiceDefinition(ServiceDefinition serviceDefinition) {
		return serviceRepository.save(serviceDefinition);
	}

	@Override
	public List<ServiceDefinition> listServices() {
		return makeCollection(serviceRepository.findAll());
	}

	@Override
	public boolean deleteServiceDefinition(String serviceDefinitionId) {
		if(!serviceRepository.exists(serviceDefinitionId)){
			return false;
		}
		ServiceDefinition serviceDefinition = serviceRepository.findOne(serviceDefinitionId);
		if(planRepository.countByServiceDefinition(serviceDefinition) > 0){
			throw new IllegalStateException("Can not remove service instance, the instance has plans associated to it");
		}
		serviceRepository.delete(serviceDefinitionId);
		return true;
	}

	
	
	
}
