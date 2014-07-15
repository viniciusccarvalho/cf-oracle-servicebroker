package com.pivotal.cf.broker.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pivotal.cf.broker.exceptions.EntityNotFoundException;
import com.pivotal.cf.broker.model.Plan;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.repositories.PlanRepository;
import com.pivotal.cf.broker.repositories.ServiceDefinitionRepository;
import com.pivotal.cf.broker.repositories.ServiceInstanceRepository;
import com.pivotal.cf.broker.services.DatabaseService;
import com.pivotal.cf.broker.services.PlanService;
import com.pivotal.cf.broker.services.TemplateService;

import freemarker.template.Configuration;

@Service
public class PlanServiceImpl implements PlanService {
	
	@Autowired
	private PlanRepository planRepository;
	
	@Autowired
	private ServiceDefinitionRepository serviceRepository;
	
	@Autowired
	private ServiceInstanceRepository instanceRepository;
	
	@Autowired
	private TemplateService templateService;
	
	
	
	@Override
	public Plan create(Plan plan) {
		ServiceDefinition serviceDefinition = serviceRepository.findOne(plan.getServiceDefinition().getId());
		if(serviceDefinition == null){
			throw new IllegalArgumentException("No such service definition : " + plan.getServiceDefinition().getId());
		}
		Map<String,Object> model = new HashMap<String, Object>();
		plan.setServiceDefinition(serviceDefinition);
		plan.getMetadata().setId(plan.getId());
		model.put("plan",plan);
		templateService.execute("plan/create.ftl", model);
		return planRepository.save(plan);
	}

	@Override
	public boolean delete(String planId) {
		Plan plan = planRepository.findOne(planId);
		if(plan == null){
			return false;
		}
		if(instanceRepository.countByPlanId(planId) > 0){
			throw new IllegalStateException("Can not remove plan, it's being used by service instances");
		}
		Map<String,Object> model = new HashMap<String, Object>();
		model.put("plan",plan);
		templateService.execute("plan/delete.ftl", model);
		planRepository.delete(plan);
		return true;
	}

}
