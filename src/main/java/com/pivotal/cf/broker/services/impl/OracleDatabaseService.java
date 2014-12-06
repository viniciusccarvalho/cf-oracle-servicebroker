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
	
	final String CREATE_PROFILE = "CREATE PROFILE %s LIMIT SESSIONS_PER_USER %s";
	final String DROP_PROFILE = "DROP PROFILE %s";
	
	final String CREATE_USER = "CREATE USER %s IDENTIFIED BY %s DEFAULT TABLESPACE %s TEMPORARY TABLESPACE %s PROFILE %s";
	final String DROP_USER = "DROP USER %s";
	final String GRANT = "grant CREATE SESSION, ALTER SESSION, CREATE DATABASE LINK, CREATE MATERIALIZED VIEW, CREATE PROCEDURE, CREATE PUBLIC SYNONYM, CREATE ROLE, CREATE SEQUENCE, CREATE SYNONYM, CREATE TABLE, CREATE TRIGGER, CREATE TYPE, CREATE VIEW, UNLIMITED TABLESPACE to %s";

	// Make initial size for tablespace configurable
	final String CREATE_TABLESPACE = "create tablespace %s datafile '%s' size %s autoextend on maxsize %s extent management local uniform size 64K";
	final String DROP_TABLESPACE =" DROP TABLESPACE %s INCLUDING CONTENTS AND DATAFILES";
	
	final String CREATE_TEMP_TABLESPACE = "create temporary tablespace %s tempfile '%s' size 10M autoextend on next 32m maxsize %s extent management local";
	
	final String BROKER_PREFIX ="oracle_broker_";
	
	@Autowired
	public OracleDatabaseService(@Qualifier("adminDs") DataSource adminDs) {
		this.template = new JdbcTemplate(adminDs);
	}
	
	@Override
	public boolean createProfile(Plan plan) {
		template.execute(String.format(CREATE_PROFILE,BROKER_PREFIX+plan.getName(), plan.getMetadata().getOther().get("connections")));
		return true;
	}

	@Override
	public boolean deleteProfile(Plan plan) {
		String command = String.format(DROP_PROFILE, BROKER_PREFIX+plan.getName());
		template.execute(command);
		return true;
	}

	@Override
	public boolean createTableSpaces(ServiceInstance instance) {
		Plan plan = planRepository.findOne(instance.getPlanId());
		String tablespaceName = StringUtils.randomString(12);
		instance.getConfig().put("tablespace",tablespaceName);

        // Adding init_size for tablespace
        String init_size = plan.getMetadata().getOther().get("init_size");
        if (init_size == null)
          init_size = "64M";
        instance.getConfig().put("init_size",init_size);

		instance.getConfig().put("tablespace",tablespaceName);
		String command = String.format(CREATE_TABLESPACE, tablespaceName, tablespaceName+".dat",init_size, plan.getMetadata().getOther().get("max_size"));
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
		Plan plan = planRepository.findOne(instance.getPlanId());
		String tablespace = instance.getConfig().get("tablespace");
		String command = String.format(CREATE_USER, binding.getCredentials().get("username"), binding.getCredentials().get("password"),tablespace,tablespace+"_temp",BROKER_PREFIX+plan.getName());
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
