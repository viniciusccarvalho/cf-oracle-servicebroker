package com.pivotal.cf.broker.services;

import com.pivotal.cf.broker.model.Plan;

public interface PlanService {
	public Plan create(Plan plan);
	public boolean delete(String planId);
}
