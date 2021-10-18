package com.tech26.robotfactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * @author remya
 * 
 * Application to Order configurable Robots
 */
@SpringBootApplication
public class RobotFatoryServicesApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(RobotFatoryServicesApplication.class, args);
	}


	  @Override
	  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	      return builder.sources(RobotFatoryServicesApplication.class);
	  }
}
