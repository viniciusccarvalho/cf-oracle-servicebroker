package com.pivotal.cf.broker.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.pivotal.cf.broker.model.Plan;

public interface PlanRepository extends CrudRepository<Plan, String> {

}
