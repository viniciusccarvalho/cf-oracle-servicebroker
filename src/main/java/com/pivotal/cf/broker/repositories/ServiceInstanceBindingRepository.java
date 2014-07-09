package com.pivotal.cf.broker.repositories;

import org.springframework.data.repository.CrudRepository;

import com.pivotal.cf.broker.model.ServiceInstanceBinding;

public interface ServiceInstanceBindingRepository extends CrudRepository<ServiceInstanceBinding, String> {
	public Long countByServiceInstanceId(String serviceInstanceId);
}
