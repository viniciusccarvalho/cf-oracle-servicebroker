package com.pivotal.cf.broker.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.pivotal.cf.broker.exceptions.EntityNotFoundException;
import com.pivotal.cf.broker.model.CreateServiceInstanceRequest;
import com.pivotal.cf.broker.model.Plan;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;
import com.pivotal.cf.broker.model.ServiceInstanceBindingRequest;
import com.pivotal.cf.broker.repositories.PlanRepository;
import com.pivotal.cf.broker.repositories.ServiceDefinitionRepository;
import com.pivotal.cf.broker.repositories.ServiceInstanceBindingRepository;
import com.pivotal.cf.broker.repositories.ServiceInstanceRepository;
import com.pivotal.cf.broker.services.BaseService;
import com.pivotal.cf.broker.services.DatabaseService;
import com.pivotal.cf.broker.services.ServiceManagement;
import com.pivotal.cf.broker.services.TemplateService;
import com.pivotal.cf.broker.utils.StringUtils;

@Service
public class ServiceManagementImpl extends BaseService implements ServiceManagement {
	@Autowired
	private PlanRepository planRepository;
	
	@Autowired
	private ServiceDefinitionRepository serviceRepository;
	
	@Autowired 
	private ServiceInstanceRepository serviceInstanceRepository;
	
	@Autowired 
	private ServiceInstanceBindingRepository bindingRepository;
	
	@Autowired
	TemplateService templateService;
	
	@Autowired
	private Environment env;
	
	@Override
	public ServiceInstance createInstance(CreateServiceInstanceRequest serviceRequest) {
		ServiceDefinition serviceDefinition = serviceRepository.findOne(serviceRequest.getServiceDefinitionId());
		if(serviceDefinition == null){
			throw new IllegalArgumentException("Service definition not found: " + serviceRequest.getServiceDefinitionId());
		}
		Plan plan = planRepository.findOne(serviceRequest.getPlanId());
		if(plan == null){
			throw new IllegalArgumentException("Invalid plan identifier");
		}
		if(serviceInstanceRepository.exists(serviceRequest.getServiceInstanceId())){
			throw new IllegalStateException("There's already an instance of this service");
		}
		ServiceInstance instance = new ServiceInstance(serviceRequest.getServiceInstanceId(), serviceDefinition.getId(), plan.getId(), serviceRequest.getOrganizationGuid(), serviceRequest.getSpaceGuid(), "");
		String tablespaceName = StringUtils.randomString(12);
		instance.getConfig().put("tablespace",tablespaceName);
		Map<String,Object> model = new HashMap<>();
		model.put("plan",plan);
		model.put("instance",instance);
		templateService.execute("instance/create.ftl", model);
		instance = serviceInstanceRepository.save(instance);
		return instance;
	}

	@Override
	public boolean removeServiceInstance(String serviceInstanceId) {
		if(!serviceInstanceRepository.exists(serviceInstanceId)){
			return false;
		}
		if(bindingRepository.countByServiceInstanceId(serviceInstanceId) > 0){
			throw new IllegalStateException("Can not delete service instance, there are still apps bound to it");
		}
		ServiceInstance instance = serviceInstanceRepository.findOne(serviceInstanceId);
		Map<String,Object> model = new HashMap<>();
		model.put("instance",instance);
		templateService.execute("instance/delete.ftl", model);
		
		serviceInstanceRepository.delete(serviceInstanceId);
		return true;
	}

	@Override
	public List<ServiceInstance> listInstances() {
		return makeCollection(serviceInstanceRepository.findAll());
	}

	@Override
	public ServiceInstanceBinding createInstanceBinding(ServiceInstanceBindingRequest bindingRequest) {
		if(bindingRepository.exists(bindingRequest.getBindingId())){
			throw new IllegalStateException("Binding Already exists");
		}
		
		Map<String, String> credentials = new HashMap<>();
		credentials.put("username", StringUtils.randomString(8));
		credentials.put("password",StringUtils.randomString(12));
		credentials.put("hostname", env.getProperty("oracle.host"));
		credentials.put("port", env.getProperty("oracle.port"));
		credentials.put("name",env.getProperty("oracle.service"));
		credentials.put("uri","oracle://"+credentials.get("username")+":"+credentials.get("password")+"@"+credentials.get("hostname")+":"+credentials.get("port")+"/"+credentials.get("name"));
		ServiceInstanceBinding binding = new ServiceInstanceBinding();
		binding.setId(bindingRequest.getBindingId());
		binding.setServiceInstanceId(bindingRequest.getInstanceId());
		binding.setAppGuid(bindingRequest.getAppGuid());
		binding.setCredentials(credentials);
		ServiceInstance instance = serviceInstanceRepository.findOne(bindingRequest.getInstanceId());
		Plan plan = planRepository.findOne(bindingRequest.getPlanId());
		Map<String,Object> model = new HashMap<>();
		model.put("plan",plan);
		model.put("binding",binding);
		model.put("instance",instance);
		templateService.execute("binding/create.ftl", model);
		binding = bindingRepository.save(binding);
		return binding;
	}

	@Override
	public boolean removeBinding(String serviceBindingId) {
		ServiceInstanceBinding binding = bindingRepository.findOne(serviceBindingId);
		if(binding == null){
			return false;
		}
		Map<String,Object> model = new HashMap<>();
		model.put("binding",binding);
		templateService.execute("binding/delete.ftl", model);
		bindingRepository.delete(binding);
		return true;
	}

	@Override
	public List<ServiceInstanceBinding> listBindings() {
		// TODO Auto-generated method stub
		return null;
	}

}
