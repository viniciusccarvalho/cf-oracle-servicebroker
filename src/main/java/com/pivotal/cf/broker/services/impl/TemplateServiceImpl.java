package com.pivotal.cf.broker.services.impl;

import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.pivotal.cf.broker.services.TemplateService;

import freemarker.template.Configuration;

@Service
public class TemplateServiceImpl implements TemplateService {

	@Autowired
	private Configuration configuration;
	
	private JdbcTemplate template;
	
	private Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);
	
	@Autowired
	public TemplateServiceImpl(@Qualifier("adminDs") DataSource adminDs) {
		this.template = new JdbcTemplate(adminDs);
	}
	
	/* (non-Javadoc)
	 * @see com.pivotal.cf.broker.services.impl.TemplateService#execute(java.lang.String, java.util.Map)
	 */
	@Override
	public boolean execute(String templateName, Map<String, Object> model){
		try {
			String sql = FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate(templateName), model).replaceAll("\\n", " ");
			logger.debug("original sql {}",sql);
			String[] commands = sql.split(";");
			for(String command : commands){
				logger.debug("executing {}",command);
				if(command.trim().length() > 0){
					template.execute(command.trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
