package com.eunsilson.homemonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HomemonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomemonitoringApplication.class, args);
	}

}
