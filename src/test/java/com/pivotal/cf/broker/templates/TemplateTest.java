package com.pivotal.cf.broker.templates;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.pivotal.cf.broker.config.AppConfig;
import com.pivotal.cf.broker.model.Plan;
import com.pivotal.cf.broker.model.PlanMetadata;
import com.pivotal.cf.broker.model.ServiceInstance;

import freemarker.template.Configuration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={AppConfig.class})
public class TemplateTest {

	@Autowired
	private Configuration configuration;
	
	private Plan plan;
	private ServiceInstance instance;
	
	@Before
	public void before(){
		this.plan = new Plan();
		plan.setName("Development");
		PlanMetadata metadata = new PlanMetadata();
		plan.setMetadata(metadata);
		metadata.getOther().put("connections", "5");
		metadata.getOther().put("max_size", "500M");
		
		this.instance = new ServiceInstance("123", "1234", "12345", "123456", "1234567", "");
		this.instance.getConfig().put("tablespace", "test");
	}
	
	@Test
	public void createPlan() throws Exception{
		
		HashMap<String, Object> model = new HashMap<>();
		model.put("plan",plan);
		String output = FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("plan/create.ftl"), model);
		System.out.println(output);
		
	}
	
	@Test
	public void createInstance() throws Exception{
		
		HashMap<String, Object> model = new HashMap<>();
		model.put("plan",plan);
		model.put("instance",instance);
		String output = FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("instance/create.ftl"), model).replaceAll("\\n", " ");
		String[] commands = output.split(";");
		for(String command : commands){
			System.out.println(command.replaceAll(";", "").trim());
		}
	
		
	}
	
}
