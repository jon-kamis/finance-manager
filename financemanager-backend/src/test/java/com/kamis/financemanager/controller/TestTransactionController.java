package com.kamis.financemanager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;

import com.kamis.financemanager.AppTestUtils;
import com.kamis.financemanager.business.impl.BusinessTestUtils;
import com.kamis.financemanager.database.domain.Role;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.RoleRepository;
import com.kamis.financemanager.database.repository.TransactionRepository;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.transactions.PagedTransactionResponse;
import com.kamis.financemanager.security.jwt.JwtService;
import com.kamis.financemanager.security.jwt.JwtTestUtils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.transaction.Transactional;

@ActiveProfiles(profiles = "test")
@SpringJUnitConfig
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestTransactionController {

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
	@Transactional
	public void setup() {

		userRepository.deleteAll();
		roleRepository.deleteAll();
		transactionRepository.deleteAll();

		RestAssured.baseURI = "http://localhost:" + port + "/api";

		Role adminRole = roleRepository.saveAndFlush(AppTestUtils.buildRole("admin"));
		Role userRole = roleRepository.saveAndFlush(AppTestUtils.buildRole("user"));

		User admin = AppTestUtils.buildUser("admin", adminRole);
		User user = AppTestUtils.buildUser("user", userRole);

		userRepository.save(admin);
		userRepository.save(user);

	}

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Test
	public void TestGetAllUserTransactions() {

		String adminUsername = "admin";
		String userUsername = "user";

		Optional<User> admin = userRepository.findByUsername(adminUsername);
		Optional<User> user = userRepository.findByUsername(userUsername);

		if (admin.isEmpty() || user.isEmpty()) {
			throw new FinanceManagerException("expected test user does not exist during pre-test setup");
		}

		transactionRepository.save(BusinessTestUtils.mockBillTransaction(admin.get().getId(), "testBill"));
		transactionRepository.save(BusinessTestUtils.mockIncomeTransaction(admin.get().getId(), "testPaycheck"));
		transactionRepository.save(BusinessTestUtils.mockBenefitTransaction(admin.get().getId(), "testBenefit"));

		RequestSpecification requestSpec = RestAssured.given();

		requestSpec.header("Content-Type", "application/json");
		requestSpec.headers(JwtTestUtils.getMockAuthHeader(jwtService.generateToken(adminUsername)));
		requestSpec.queryParam("page", 1);
		requestSpec.queryParam("pageSize", 2);

		Response response = requestSpec.get("/users/" + admin.get().getId() + "/transactions");
		assertEquals(200, response.statusCode());

		PagedTransactionResponse respBody = response.getBody().as(PagedTransactionResponse.class);
		assertEquals(3, respBody.getCount());
		assertEquals(2, respBody.getPageSize());
		assertEquals(1, respBody.getPage());

	}

}
