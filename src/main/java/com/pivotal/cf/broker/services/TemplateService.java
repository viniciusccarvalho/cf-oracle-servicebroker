package com.pivotal.cf.broker.services;

import java.util.Map;

public interface TemplateService {

	public abstract boolean execute(String templateName, Map<String, Object> model);

}