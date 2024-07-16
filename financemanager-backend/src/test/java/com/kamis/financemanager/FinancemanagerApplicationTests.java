package com.kamis.financemanager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.kamis.financemanager.database.repository.LoanPaymentRepository;
import com.kamis.financemanager.database.repository.LoanRepository;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.database.repository.UserRoleRepository;
	
public class FinancemanagerApplicationTests {

	@Autowired
	private LoanPaymentRepository loanPaymentRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	@LocalServerPort
	private Integer port;

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

	@BeforeAll
	static void beforeAll() {
		postgres.start();
	}

	@AfterAll
	static void afterAll() {
		postgres.stop();
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@BeforeEach
	void setUp() {
		loanPaymentRepository.deleteAll();
		loanRepository.deleteAll();
		userRoleRepository.deleteAll();
		userRepository.deleteAll();
	}

}
