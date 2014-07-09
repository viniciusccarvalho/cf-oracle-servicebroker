package com.pivotal.cf.broker.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * An instance of a ServiceDefinition.
 * 
 * @author sgreenberg@gopivotal.com
 * @author vcarvalho@gopivotal.com
 *
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@Entity
public class ServiceInstance {

	@JsonSerialize
	@JsonProperty("service_instance_id")
	@Id
	private String id;
	
	@JsonSerialize
	@JsonProperty("service_id")
	@Column(name="service_id")
	private String serviceDefinitionId;
	
	@JsonSerialize
	@JsonProperty("plan_id")
	@Column(name="plan_id")
	private String planId;
	
	@JsonSerialize
	@JsonProperty("organization_guid")
	@Column(name="organization_guid")
	private String organizationGuid;
	
	@JsonSerialize
	@JsonProperty("space_guid")
	@Column(name="space_guid")
	private String spaceGuid;
	
	@JsonSerialize
	@JsonProperty("dashboard_url")
	@Column(name="dashboard_url")
	private String dashboardUrl;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@MapKeyColumn(name="key")
	@Column(name="value")
	@CollectionTable(name="service_instance_config", joinColumns = {@JoinColumn(name="sid")})
	@JsonIgnore
	private Map<String,String> config = new HashMap<String,String>();
	

	@SuppressWarnings("unused")
	private ServiceInstance() {}
	
	public ServiceInstance( String id, String serviceDefinitionId, String planId, String organizationGuid, String spaceGuid, String dashboardUrl ) {
		setId(id);
		setServiceDefinitionId(serviceDefinitionId);
		setPlanId(planId);
		setOrganizationGuid(organizationGuid);
		setSpaceGuid(spaceGuid);
		setDashboardUrl(dashboardUrl);
	}
	
	public String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	private void setServiceDefinitionId(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public String getPlanId() {
		return planId;
	}

	private void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getOrganizationGuid() {
		return organizationGuid;
	}

	private void setOrganizationGuid(String organizationGuid) {
		this.organizationGuid = organizationGuid;
	}

	public String getSpaceGuid() {
		return spaceGuid;
	}

	private void setSpaceGuid(String spaceGuid) {
		this.spaceGuid = spaceGuid;
	}

	public String getDashboardUrl() {
		return dashboardUrl;
	}

	private void setDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}

	public Map<String, String> getConfig() {
		return config;
	}

	public void setConfig(Map<String, String> config) {
		this.config = config;
	}
	
}
