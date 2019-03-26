package com.info.email.config;


import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties("spring.datasource")
@Getter
@Setter
public class DatabaseConfiguration {
	
private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private Environment env;
	
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	
	@Profile("dev")
	@Bean
	public DataSource devDataSource(){
		LOGGER.info("DB connection for Development");
		return createDataSource();
	}

	@Profile("test")
	@Bean
	public DataSource testDataSource() {
		LOGGER.info("DB Connection fot Test");
		return createDataSource();
	}

	@Profile("prod")
	@Bean
	public DataSource prodDataSource(){
		LOGGER.info("DB Connection to Production");
		return createDataSource();
	}

	
	private DataSource createDataSource() {
		return DataSourceBuilder
				.create()
				.username(username)
				.password(password)
				.url(url)
				.driverClassName(driverClassName)
				.build();
	}


}
