package com.pivotal.cf.broker.config;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.pivotal.cf.broker.model.Plan;

@Configuration
@ComponentScan(basePackages = "com.pivotal.cf.broker")
@EnableJpaRepositories(basePackageClasses = Plan.class)
@EnableTransactionManagement
@EntityScan(basePackageClasses=Plan.class)
public class AppConfig {

	@Autowired
	private Environment env;

	@Bean
	@Primary
	public DataSource datasource() throws Exception {
		PoolProperties p = new PoolProperties();
		p.setDriverClassName(env.getProperty("broker.datasource.driverClassName"));
		p.setUsername(env.getProperty("broker.datasource.username"));
		p.setPassword(env.getProperty("broker.datasource.password"));
		p.setUrl(env.getProperty("broker.datasource.url"));
		p.setMaxActive(Integer.valueOf(env.getProperty("broker.datasource.max-active")));
		p.setMinIdle(Integer.valueOf(env.getProperty("broker.datasource.min-idle")));
		p.setMaxIdle(Integer.valueOf(env.getProperty("broker.datasource.max-idle")));
		p.setInitialSize(Integer.valueOf(env.getProperty("broker.datasource.initial-size")));
		org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
		ds.setPoolProperties(p);
		return ds;
	}

	@Bean(name = "adminDs")
	public DataSource adminDs() throws Exception {
		PoolProperties p = new PoolProperties();
		p.setDriverClassName(env.getProperty("dba.datasource.driverClassName"));
		p.setUsername(env.getProperty("dba.datasource.username"));
		p.setPassword(env.getProperty("dba.datasource.password"));
		p.setUrl(env.getProperty("dba.datasource.url"));
		p.setMaxActive(Integer.valueOf(env.getProperty("dba.datasource.max-active")));
		p.setMinIdle(Integer.valueOf(env.getProperty("dba.datasource.min-idle")));
		p.setMaxIdle(Integer.valueOf(env.getProperty("dba.datasource.max-idle")));
		p.setInitialSize(Integer.valueOf(env.getProperty("dba.datasource.initial-size")));
		org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
		ds.setPoolProperties(p);
		return ds;
	}

}
