package com.kamis.financemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableAsync
@EnableScheduling
@EnableMethodSecurity
@SpringBootApplication
public class FinancemanagerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(FinancemanagerApplication.class, args);
	}

}
