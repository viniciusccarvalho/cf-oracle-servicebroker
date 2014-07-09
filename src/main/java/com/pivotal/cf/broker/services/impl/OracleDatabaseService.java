package com.pivotal.cf.broker.services.impl;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.pivotal.cf.broker.model.Plan;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;
import com.pivotal.cf.broker.repositories.PlanRepository;
import com.pivotal.cf.broker.repositories.ServiceInstanceRepository;
import com.pivotal.cf.broker.services.DatabaseService;
import com.pivotal.cf.broker.utils.StringUtils;

@Service
public class OracleDatabaseService implements DatabaseService {
		
	private JdbcTemplate template;
	
	@Autowired
	private PlanRepository planRepository;
	
	@Autowired
	private ServiceInstanceRepository serviceInstanceRepository;
	
	final Logger logger = LoggerFactory.getLogger(OracleDatabaseService.class);
	
	final String CREATE_PROFILE = "CREATE USER_PROFILE";
	
	final String CREATE_USER = "CREATE USER %s IDENTIFIED BY %s DEFAULT TABLESPACE %s TEMPORARY TABLESPACE %s";
	final String DROP_USER = "DROP USER %s";
	final String GRANT = "grant CREATE SESSION, ALTER SESSION, CREATE DATABASE LINK, CREATE MATERIALIZED VIEW, CREATE PROCEDURE, CREATE PUBLIC SYNONYM, CREATE ROLE, CREATE SEQUENCE, CREATE SYNONYM, CREATE TABLE, CREATE TRIGGER, CREATE TYPE, CREATE VIEW, UNLIMITED TABLESPACE to %s";
	final String CREATE_TABLESPACE = "create tablespace %s datafile '%s' size 10M autoextend on maxsize %s extent management local uniform size 64K";
	final String DROP_TABLESPACE =" DROP TABLESPACE %s INCLUDING CONTENTS AND DATAFILES";
	
	final String CREATE_TEMP_TABLESPACE = "create temporary tablespace %s tempfile '%s' size 10M autoextend on next 32m maxsize %s extent management local";
	
	@Autowired
	public OracleDatabaseService(@Qualifier("adminDs") DataSource adminDs) {
		this.template = new JdbcTemplate(adminDs);
	}
	
	@Override
	public boolean createProfile(Plan plan) {
		//template.execute(String.format(CREATE_PROFILE,plan.getId(), plan.getMetadata().get("max_connections")));
		return true;
	}

	@Override
	public boolean createTableSpaces(ServiceInstance instance) {
		Plan plan = planRepository.findOne(instance.getPlanId());
		String tablespaceName = StringUtils.randomString(12);
		instance.getConfig().put("tablespace",tablespaceName);
		String command = String.format(CREATE_TABLESPACE, tablespaceName, tablespaceName+".dat",plan.getMetadata().getOther().get("max_size"));
		String tempCommand = String.format(CREATE_TEMP_TABLESPACE,tablespaceName+"_temp",tablespaceName+"_temp.dat",plan.getMetadata().getOther().get("max_size"));
		logger.debug(command);
		logger.debug(tempCommand);
		template.execute(command);
		template.execute(tempCommand);
		return true;
	}

	@Override
	public boolean createUser(ServiceInstanceBinding binding) {
		ServiceInstance instance = serviceInstanceRepository.findOne(binding.getServiceInstanceId());
		String tablespace = instance.getConfig().get("tablespace");
		String command = String.format(CREATE_USER, binding.getCredentials().get("username"), binding.getCredentials().get("password"),tablespace,tablespace+"_temp");
		String grantCommand = String.format(GRANT, binding.getCredentials().get("username"));
		logger.debug(command);
		logger.debug(grantCommand);
		template.execute(command);
		template.execute(grantCommand);
		return true;
	}

	@Override
	public boolean deleteTableSpaces(String serviceInstanceId) {
		ServiceInstance instance = serviceInstanceRepository.findOne(serviceInstanceId);
		String tablespace = instance.getConfig().get("tablespace");
		String command = String.format(DROP_TABLESPACE, tablespace);
		String tempCommand = String.format(DROP_TABLESPACE, tablespace+"_temp");
		logger.debug(command);
		logger.debug(tempCommand);
		template.execute(command);
		template.execute(tempCommand);
		return true;
	}

	@Override
	public boolean deleteUser(ServiceInstanceBinding binding) {
		String command = String.format(DROP_USER, binding.getCredentials().get("username"));
		logger.debug(command);
		template.execute(command);
		return true;
	}

}
