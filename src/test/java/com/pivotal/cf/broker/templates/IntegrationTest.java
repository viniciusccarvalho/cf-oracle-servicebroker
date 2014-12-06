package com.pivotal.cf.broker.templates;

import java.util.Arrays;
import java.util.UUID;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.pivotal.cf.broker.model.Catalog;
import com.pivotal.cf.broker.model.CreateServiceInstanceRequest;
import com.pivotal.cf.broker.model.Plan;
import com.pivotal.cf.broker.model.PlanMetadata;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBindingRequest;
import com.pivotal.cf.broker.model.ServiceInstanceBindingResponse;

public class IntegrationTest {

	@Test
	public void validateEndpoints() throws Exception {
		RestTemplate template = new RestTemplate();
		String host = "http://localhost:8080";
		ResponseEntity<Catalog> catalogResponse = template.getForEntity(host+"/v2/catalog", Catalog.class);
		ServiceDefinition definition = catalogResponse.getBody().getServiceDefinitions().get(0);

		Plan plan = new Plan();
		plan.setName("dev");
		plan.setDescription("development plan");
		PlanMetadata metadata = new PlanMetadata();
		metadata.setBullets(Arrays.asList("250 megabytes of space","5 simultaneous connections"));
		metadata.getOther().put("connections", "5");
		metadata.getOther().put("init_size","20M");
		metadata.getOther().put("max_size","250M");
		plan.setMetadata(metadata);
		
		ResponseEntity<Plan> planResponse = template.postForEntity(host+"/v2/catalog/services/"+definition.getId()+"/plans", plan, Plan.class);
		
		template.put(host+"/v2/service_instances/oratest", new CreateServiceInstanceRequest( definition.getId(), planResponse.getBody().getId(), UUID.randomUUID().toString(), UUID.randomUUID().toString()));
		ServiceInstanceBindingRequest bindingRequest = new ServiceInstanceBindingRequest(definition.getId(),plan.getId(),UUID.randomUUID().toString(),UUID.randomUUID().toString());
		
		ResponseEntity<ServiceInstanceBindingResponse> bindingResponse = template.exchange(host+"/v2/service_instances/oratest/service_bindings/appbinding", HttpMethod.PUT, new HttpEntity<ServiceInstanceBindingRequest>(bindingRequest), ServiceInstanceBindingResponse.class);
		System.out.println(bindingResponse.getBody());
		template.delete(host+"/v2/service_instances/oratest/service_bindings/appbinding");
		template.delete(host+"/v2/service_instances/oratest");
	}
	
}
