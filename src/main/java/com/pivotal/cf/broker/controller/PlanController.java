package com.pivotal.cf.broker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pivotal.cf.broker.model.Plan;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.services.CatalogService;
import com.pivotal.cf.broker.services.PlanService;

@Controller
@RequestMapping(value="/v2/catalog/services/{sid}/plans")
public class PlanController {
	
	@Autowired
	private PlanService service;
	
	private static final Logger logger = LoggerFactory.getLogger(PlanController.class);
	
	
	@ResponseBody
	@RequestMapping(consumes="application/json", produces="application/json", method=RequestMethod.POST)
	public ResponseEntity<Plan> createPlan(@RequestBody Plan plan, @PathVariable("sid") String sid){
		ServiceDefinition serviceDefinition = new ServiceDefinition(sid);
		plan.setServiceDefinition(serviceDefinition);
		Plan persistedPlan = service.create(plan);
		ResponseEntity<Plan> response= new ResponseEntity<>(persistedPlan, HttpStatus.CREATED);
		return response;
	}
	
	@RequestMapping(value="/{planId}",produces="application/json", method=RequestMethod.DELETE)
	public ResponseEntity<String> deletePlan(@PathVariable("sid") String sid, @PathVariable("planId") String planId){
		boolean deleted = service.delete(planId);
		HttpStatus status = deleted ? HttpStatus.OK : HttpStatus.GONE;
		ResponseEntity<String> response = new ResponseEntity<>("{}",status);  
		return response;
	}
	
	
	
	
}
