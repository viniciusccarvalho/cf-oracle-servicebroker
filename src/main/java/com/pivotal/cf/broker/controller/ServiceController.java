package com.pivotal.cf.broker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pivotal.cf.broker.model.CreateServiceInstanceRequest;
import com.pivotal.cf.broker.model.CreateServiceInstanceResponse;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;
import com.pivotal.cf.broker.model.ServiceInstanceBindingRequest;
import com.pivotal.cf.broker.model.ServiceInstanceBindingResponse;
import com.pivotal.cf.broker.services.ServiceManagement;

@Controller
@RequestMapping("/v2/service_instances")
public class ServiceController {

	@Autowired
	private ServiceManagement service;
	
	@ResponseBody
	@RequestMapping(consumes="application/json", produces="application/json", value="/{instanceId}", method=RequestMethod.PUT)
	public ResponseEntity<CreateServiceInstanceResponse> provision(@RequestBody CreateServiceInstanceRequest serviceRequest, @PathVariable("instanceId") String serviceInstanceId) {
		serviceRequest.setServiceInstanceId(serviceInstanceId);
		ServiceInstance instance = service.createInstance(serviceRequest);
		ResponseEntity<CreateServiceInstanceResponse> response = new ResponseEntity<>(new CreateServiceInstanceResponse(instance),HttpStatus.CREATED);  
		return response;
	}
	
	@RequestMapping(produces="application/json", value="/{instanceId}", method=RequestMethod.DELETE)
	public ResponseEntity<String> deprovision(@PathVariable("instanceId") String serviceInstanceId) {
		boolean deleted = service.removeServiceInstance(serviceInstanceId);
		HttpStatus status = deleted ? HttpStatus.OK : HttpStatus.GONE;
		ResponseEntity<String> response = new ResponseEntity<>("{}",status);  
		return response;
	}
	
	@ResponseBody
	@RequestMapping(produces="application/json", method=RequestMethod.GET)
	public ResponseEntity<List<ServiceInstance>> listInstances(){
		ResponseEntity<List<ServiceInstance>> response = new ResponseEntity<>(service.listInstances(),HttpStatus.OK);
		return response;
	}
	
	@ResponseBody
	@RequestMapping(value="/{instanceId}/service_bindings/{bindingId}",consumes="application/json", produces="application/json", method=RequestMethod.PUT)
	public ResponseEntity<ServiceInstanceBindingResponse> bind(@RequestBody ServiceInstanceBindingRequest request, @PathVariable("instanceId") String serviceInstanceId, @PathVariable("bindingId") String bindingId){
		request.setBindingId(bindingId);
		request.setInstanceId(serviceInstanceId);
		ServiceInstanceBinding binding = service.createInstanceBinding(request);
		return new ResponseEntity<ServiceInstanceBindingResponse>(
        		new ServiceInstanceBindingResponse(binding), 
        		HttpStatus.CREATED);
	}
	
	@RequestMapping(value="/{instanceId}/service_bindings/{bindingId}", produces="application/json", method=RequestMethod.DELETE)
	public ResponseEntity<String> unbind(@PathVariable("instanceId") String serviceInstanceId, @PathVariable("bindingId") String bindingId){
		boolean deleted = service.removeBinding(bindingId);
		HttpStatus status = deleted ? HttpStatus.OK : HttpStatus.GONE;
		ResponseEntity<String> response = new ResponseEntity<>("{}",status);  
		return response;
	}

	
}
