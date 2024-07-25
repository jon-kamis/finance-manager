package com.kamis.financemanager.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDate;
import java.util.List;
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
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestLoanController {

	@LocalServerPort
	private Integer port;

	private static final String TEST_PREFIX = "LoanController";

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
	private void setup() {
		RestAssured.baseURI = "http://localhost:" + port + "/api";

		cleanData();
		insertData();
	}

	private void cleanData() {
		Optional<User> optAdmin = userRepository.findByUsername(TEST_PREFIX + "_admin");
		Optional<User> optUser = userRepository.findByUsername(TEST_PREFIX + "_user");

		if (optAdmin.isPresent()) {
			userRepository.delete(optAdmin.get());
		}

		if (optUser.isPresent()) {
			userRepository.delete(optUser.get());
		}

	}

	private void insertData() {
		Optional<Role> adminRole = roleRepository.findByName("admin");
		Optional<Role> userRole = roleRepository.findByName("user");

		if (adminRole.isEmpty()) {
			roleRepository.saveAndFlush(AppTestUtils.buildRole("admin"));
			adminRole = roleRepository.findByName("admin");
		}

		if (userRole.isEmpty()) {
			roleRepository.saveAndFlush(AppTestUtils.buildRole("user"));
			userRole = roleRepository.findByName("user");
		}

		User admin = AppTestUtils.buildUser(TEST_PREFIX + "_admin", adminRole.get());
		User user = AppTestUtils.buildUser(TEST_PREFIX + "_user", userRole.get());

		admin = userRepository.saveAndFlush(admin);
		user = userRepository.saveAndFlush(user);

		Loan loan1 = AppTestUtils.buildLoan(user, user.getUsername() + "_loan", 100, 10, LocalDate.now());
		Loan loan2 = AppTestUtils.buildLoan(admin, admin.getUsername() + "_loan", 100, 10, LocalDate.now());

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
	@WithMockUser(username = "admin", authorities = { "admin", "user" })
	public void TestGetLoanById_admin() {

		String adminUsername = TEST_PREFIX + "_admin";
		String userUsername = TEST_PREFIX + "_user";

		Optional<User> admin = userRepository.findByUsername(adminUsername);
		Optional<User> user = userRepository.findByUsername(userUsername);

		if (admin.isEmpty() || user.isEmpty()) {
			throw new FinanceManagerException("expected test user does not exist during pre-test setup");
		}

		List<Loan> adminLoans = loanRepository.findByUserId(admin.get().getId());
		List<Loan> userLoans = loanRepository.findByUserId(user.get().getId());

		if (adminLoans == null || adminLoans.size() == 0) {
			throw new FinanceManagerException("admin loan was not found during pre-test setup");
		}

		if (userLoans == null || userLoans.size() == 0) {
			throw new FinanceManagerException("user loan was not found during pre-test setup");
		}

		Loan adminLoan = adminLoans.get(0);
		Loan userLoan = userLoans.get(0);

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
	@WithMockUser(username = "user", authorities = { "user" })
	public void TestGetLoanById_user() {

		String adminUsername = TEST_PREFIX + "_admin";
		String userUsername = TEST_PREFIX + "_user";

		Optional<User> admin = userRepository.findByUsername(adminUsername);
		Optional<User> user = userRepository.findByUsername(userUsername);

		if (admin.isEmpty() || user.isEmpty()) {
			throw new FinanceManagerException("expected test user does not exist during pre-test setup");
		}

		List<Loan> adminLoans = loanRepository.findByUserId(admin.get().getId());
		List<Loan> userLoans = loanRepository.findByUserId(user.get().getId());

		if (adminLoans == null || adminLoans.size() == 0) {
			throw new FinanceManagerException("admin loan was not found during pre-test setup");
		}

		if (userLoans == null || userLoans.size() == 0) {
			throw new FinanceManagerException("user loan was not found during pre-test setup");
		}

		Loan adminLoan = adminLoans.get(0);
		Loan userLoan = userLoans.get(0);

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
