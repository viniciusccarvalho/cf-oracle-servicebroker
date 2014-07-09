package com.pivotal.cf.broker.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pivotal.cf.broker.model.Catalog;
import com.pivotal.cf.broker.model.CreateServiceInstanceRequest;
import com.pivotal.cf.broker.model.CreateServiceInstanceResponse;
import com.pivotal.cf.broker.model.Plan;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBindingRequest;
import com.pivotal.cf.broker.services.CatalogService;

@Controller
@RequestMapping(value="/v2/catalog")
public class CatalogController {
	
	@Autowired
	private CatalogService service;
	
	private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);
	
	@ResponseBody
	@RequestMapping(consumes="application/json", produces="application/json", value="/services", method=RequestMethod.POST)
	public ServiceDefinition createServiceDefinition(@RequestBody ServiceDefinition serviceDefinition){
		return service.createServiceDefinition(serviceDefinition);
	}
	
	@ResponseBody
	@RequestMapping(produces="application/json", value="/services/{sid}", method=RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceDefinition(@PathVariable("sid") String serviceInstanceId){
		boolean deleted = service.deleteServiceDefinition(serviceInstanceId);
		HttpStatus status = deleted ? HttpStatus.OK : HttpStatus.GONE;
		ResponseEntity<String> response = new ResponseEntity<>("{}",status);  
		return response;
	}
	
	@ResponseBody
	@RequestMapping(produces="application/json", method=RequestMethod.GET)
	public Catalog list(){
		List<ServiceDefinition> services = service.listServices();
		Catalog catalog = new Catalog(services);
		return catalog;
	}
}
