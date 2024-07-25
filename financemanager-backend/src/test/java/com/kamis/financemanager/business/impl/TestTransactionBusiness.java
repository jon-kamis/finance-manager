package com.kamis.financemanager.business.impl;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import com.kamis.financemanager.AppTestUtils;
import com.kamis.financemanager.database.domain.Role;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.RoleRepository;
import com.kamis.financemanager.database.repository.TransactionRepository;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.database.repository.UserRoleRepository;
import com.kamis.financemanager.exception.FinanceManagerException;

import jakarta.transaction.Transactional;

@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestTransactionBusiness {

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
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
	@Transactional
	public void setUp() {

		userRoleRepository.deleteAll();
		roleRepository.deleteAll();
		transactionRepository.deleteAll();
		userRepository.deleteAll();

		roleRepository.saveAndFlush(AppTestUtils.buildRole("admin"));
		roleRepository.saveAndFlush(AppTestUtils.buildRole("user"));

		Optional<Role> optAdminRole = roleRepository.findByName("admin");
		Optional<Role> optUserRole = roleRepository.findByName("user");

		if (optAdminRole.isEmpty() || optUserRole.isEmpty()) {
			throw new FinanceManagerException("Failed to retrieve loans during populate data",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		userRepository.saveAndFlush(AppTestUtils.buildUser("admin", optAdminRole.get()));
		userRepository.saveAndFlush(AppTestUtils.buildUser("user", optUserRole.get()));

		Optional<User> optAdmin = userRepository.findByUsername("admin");
		Optional<User> optUser = userRepository.findByUsername("user");

		if (optAdmin.isEmpty() || optUser.isEmpty()) {
			throw new FinanceManagerException("Failed to retrieve users during populate data",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}