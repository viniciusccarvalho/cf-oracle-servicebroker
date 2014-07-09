package com.pivotal.cf.broker.repositories;

import org.springframework.data.repository.CrudRepository;

import com.pivotal.cf.broker.model.ServiceInstance;

public interface ServiceInstanceRepository extends CrudRepository<ServiceInstance, String> {
	
	public Long countByPlanId(String planId);
	
}
