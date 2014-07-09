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

/**
 * A binding to a service instance
 * 
 * @author sgreenberg@gopivotal.com
 * @author vcarvalho@gopivotal.com
 *
 */
@Entity
public class ServiceInstanceBinding {
	
	@Id
	private String id;
	private String serviceInstanceId;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@MapKeyColumn(name="key")
	@Column(name="value")
	@CollectionTable(name="binding_credentials", joinColumns = {@JoinColumn(name="binding_id")})
	private Map<String,String> credentials = new HashMap<String,String>();
	
	private String syslogDrainUrl;
	
	private String appGuid;

	public ServiceInstanceBinding(){}
	
	public ServiceInstanceBinding(String id, 
			String serviceInstanceId, 
			Map<String,String> credentials,
			String syslogDrainUrl, String appGuid) {
		this.id = id;
		this.serviceInstanceId = serviceInstanceId;
		setCredentials(credentials);
		this.syslogDrainUrl = syslogDrainUrl;
		this.appGuid = appGuid;
	}

	public String getId() {
		return id;
	}

	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public Map<String, String> getCredentials() {
		return credentials;
	}

	public void setCredentials(Map<String, String> credentials) {
		if (credentials == null) {
			credentials = new HashMap<String,String>();
		} else {
			this.credentials = credentials;
		}
	}

	public String getSyslogDrainUrl() {
		return syslogDrainUrl;
	}
	
	public String getAppGuid() {
		return appGuid;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setServiceInstanceId(String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	public void setSyslogDrainUrl(String syslogDrainUrl) {
		this.syslogDrainUrl = syslogDrainUrl;
	}

	public void setAppGuid(String appGuid) {
		this.appGuid = appGuid;
	}

	@Override
	public String toString() {
		return "ServiceInstanceBinding [id=" + id + ", serviceInstanceId=" + serviceInstanceId + ", credentials=" + credentials + ", syslogDrainUrl=" + syslogDrainUrl + ", appGuid=" + appGuid + "]";
	}
	
}
