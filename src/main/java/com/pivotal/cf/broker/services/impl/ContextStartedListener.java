package com.pivotal.cf.broker.services.impl;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.repositories.ServiceDefinitionRepository;

@Component
public class ContextStartedListener implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private ServiceDefinitionRepository repository;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			InputStream in = ContextStartedListener.class.getClassLoader().getResourceAsStream("ServiceDescription.json");
			ServiceDefinition def = mapper.readValue(in, ServiceDefinition.class);
			if(repository.count() == 0){
				repository.save(def);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
