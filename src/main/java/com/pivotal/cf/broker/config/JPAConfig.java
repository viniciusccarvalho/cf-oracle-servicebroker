package com.pivotal.cf.broker.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.pivotal.cf.broker.model.Plan;

//@Configuration
//@EnableJpaRepositories(basePackageClasses = Plan.class)
//@EnableTransactionManagement
public class JPAConfig {
/*
	@Autowired
	private JpaProperties jpaProperties;
	
	@Bean
	public EntityManagerFactory entityManagerFactory(DataSource datasource) {

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(jpaVendorAdapter());
		factory.setPackagesToScan("com.pivotal.cf.broker.model");
		factory.setDataSource(datasource);
		factory.afterPropertiesSet();

		return factory.getObject();
	}
	
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setShowSql(this.jpaProperties.isShowSql());
		adapter.setDatabase(this.jpaProperties.getDatabase());
		adapter.setDatabasePlatform(this.jpaProperties.getDatabasePlatform());
		adapter.setGenerateDdl(this.jpaProperties.isGenerateDdl());
		return adapter;
	}

	@Bean
	@Primary
	public PlatformTransactionManager transactionManager(DataSource datasource) {
		return new DataSourceTransactionManager(datasource);
	}
*/
}
