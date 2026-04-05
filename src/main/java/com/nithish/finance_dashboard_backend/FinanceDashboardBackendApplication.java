package com.nithish.finance_dashboard_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class FinanceDashboardBackendApplication {

	public static void main(String[] args) {
		var ctx = SpringApplication.run(FinanceDashboardBackendApplication.class, args);
		System.out.println("====== URI LOADED IS: " + ctx.getEnvironment().getProperty("spring.mongodb.uri") + " ======");
	}

}
