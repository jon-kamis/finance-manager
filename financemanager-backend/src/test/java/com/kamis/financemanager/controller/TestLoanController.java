package com.kamis.financemanager.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.kamis.financemanager.security.SecurityService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;

import com.kamis.financemanager.AppTestUtils;
import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.database.domain.Role;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.LoanRepository;
import com.kamis.financemanager.database.repository.RoleRepository;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.security.jwt.JwtService;
import com.kamis.financemanager.security.jwt.JwtTestUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;

@ActiveProfiles(profiles = "test")
@SpringJUnitConfig
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestLoanController {

	@LocalServerPort
	private Integer port;

	@Mock
	private SecurityService securityService;

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
	public void setup() {
		RestAssured.baseURI = "http://localhost:" + port + "/api";

		cleanData();
		insertData();
	}

	@Transactional
	private void cleanData() {
		userRepository.deleteAll();
		roleRepository.deleteAll();
		loanRepository.deleteAll();

	}

	@Transactional
	private void insertData() {

		Role adminRole = roleRepository.saveAndFlush(AppTestUtils.buildRole("admin"));
		Role userRole = roleRepository.saveAndFlush(AppTestUtils.buildRole("user"));

		User admin = AppTestUtils.buildUser("admin", adminRole);
		User user = AppTestUtils.buildUser("user", userRole);

		admin = userRepository.saveAndFlush(admin);
		user = userRepository.saveAndFlush(user);

		Loan loan1 = AppTestUtils.buildLoan(user, user.getUsername() + "_loan", 100, 10, LocalDate.now());
		Loan loan2 = AppTestUtils.buildLoan(admin, admin.getUsername() + "_loan", 100, 10, LocalDate.now());

		loan1.setCurrentPaymentNumber(1);
		loan1.setBalance((float)10);

		loan2.setCurrentPaymentNumber(1);
		loan2.setBalance((float)10);

		loanRepository.save(loan1);
		loanRepository.save(loan2);
	}

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private LoanRepository loanRepository;

	@Test
	public void TestGetLoanById_admin() {

		String adminUsername = "admin";
		String userUsername = "user";

		Optional<User> admin = userRepository.findByUsername(adminUsername);
		Optional<User> user = userRepository.findByUsername(userUsername);

		if (admin.isEmpty() || user.isEmpty()) {
			throw new FinanceManagerException("expected test user does not exist during pre-test setup");
		}

		List<Loan> adminLoans = loanRepository.findByUserId(admin.get().getId());
		List<Loan> userLoans = loanRepository.findByUserId(user.get().getId());

		if (adminLoans == null || adminLoans.isEmpty()) {
			throw new FinanceManagerException("admin loan was not found during pre-test setup");
		}

		if (userLoans == null || userLoans.isEmpty()) {
			throw new FinanceManagerException("user loan was not found during pre-test setup");
		}

		Loan adminLoan = adminLoans.getFirst();
		Loan userLoan = userLoans.getFirst();

		// Admins can retrieve loans for themselves
		given().contentType(ContentType.JSON)
				.headers(JwtTestUtils.getMockAuthHeader(jwtService.generateToken(adminUsername))).when()
				.get("/users/" + admin.get().getId() + "/loans/" + adminLoan.getId()).then().statusCode(200)
				.body("userId", equalTo(admin.get().getId())).body("id", equalTo(adminLoan.getId()));

		// Admins can retrieve loans for other users
		given().contentType(ContentType.JSON)
				.headers(JwtTestUtils.getMockAuthHeader(jwtService.generateToken(adminUsername))).when()
				.get("/users/" + user.get().getId() + "/loans/" + userLoan.getId()).then().statusCode(200)
				.body("userId", equalTo(user.get().getId())).body("id", equalTo(userLoan.getId()));
	}

	@Test
	public void TestGetLoanById_user() {

		String adminUsername = "admin";
		String userUsername = "user";

		Optional<User> admin = userRepository.findByUsername(adminUsername);
		Optional<User> user = userRepository.findByUsername(userUsername);

		if (admin.isEmpty() || user.isEmpty()) {
			throw new FinanceManagerException("expected test user does not exist during pre-test setup");
		}

		List<Loan> adminLoans = loanRepository.findByUserId(admin.get().getId());
		List<Loan> userLoans = loanRepository.findByUserId(user.get().getId());

		if (adminLoans == null || adminLoans.isEmpty()) {
			throw new FinanceManagerException("admin loan was not found during pre-test setup");
		}

		if (userLoans == null || userLoans.isEmpty()) {
			throw new FinanceManagerException("user loan was not found during pre-test setup");
		}

		Loan adminLoan = adminLoans.getFirst();
		Loan userLoan = userLoans.getFirst();

		//Users can retrieve loans for themselves
		given().contentType(ContentType.JSON)
				.headers(JwtTestUtils.getMockAuthHeader(jwtService.generateToken(userUsername))).when()
				.get("/users/" + user.get().getId() + "/loans/" + userLoan.getId()).then().statusCode(200)
				.body("userId", equalTo(user.get().getId())).body("id", equalTo(userLoan.getId()));

		//Users cannot retrieve loans for other users
		given().contentType(ContentType.JSON)
				.headers(JwtTestUtils.getMockAuthHeader(jwtService.generateToken(userUsername))).when()
				.get("/users/" + admin.get().getId() + "/loans/" + adminLoan.getId()).then().statusCode(403);
	}

}
